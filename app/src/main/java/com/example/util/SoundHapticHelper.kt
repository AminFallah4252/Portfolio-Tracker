package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val LocalSoundHaptic = staticCompositionLocalOf<SoundHapticHelper> {
    error("No SoundHapticHelper provided")
}

class SoundHapticHelper(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = appContext.getSharedPreferences("app_sound_haptic_prefs", Context.MODE_PRIVATE)
    private val audioManager: AudioManager? = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val soundScope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var toneGenerator: ToneGenerator? = null

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC_ENABLED, true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    init {
        try {
            // Use STREAM_MUSIC with 85 volume so it's always audible regardless of system ringer
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 85)
            } catch (_: Exception) {
                toneGenerator = null
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
        prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
    }

    // --- Synthesized PCM Tone for Guaranteed Audio Playback ---
    private fun playSynthesizedTone(freqHz: Double, durationMs: Int, volume: Float = 0.8f) {
        if (!_soundEnabled.value) return
        soundScope.launch {
            try {
                val sampleRate = 44100
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val generatedSnd = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
                    val envelope = when {
                        i < sampleRate * 0.005 -> (i / (sampleRate * 0.005)).toFloat() // Attack
                        i > numSamples - sampleRate * 0.01 -> ((numSamples - i) / (sampleRate * 0.01)).toFloat() // Release
                        else -> 1.0f
                    }
                    val sample = (Math.sin(angle) * Short.MAX_VALUE * volume * envelope).toInt()
                    generatedSnd[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(numSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, numSamples)
                audioTrack.play()
                // Auto-release after playing
                kotlinx.coroutines.delay(durationMs.toLong() + 30)
                audioTrack.release()
            } catch (e: Exception) {
                // Fallback to ToneGenerator or system click
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs)
                } catch (_: Exception) {}
            }
        }
    }

    // --- Sound Effects ---
    fun playClick() {
        if (!_soundEnabled.value) return
        playSynthesizedTone(750.0, 25, 0.6f)
    }

    fun playKeypad() {
        if (!_soundEnabled.value) return
        playSynthesizedTone(920.0, 20, 0.7f)
    }

    fun playSuccess() {
        if (!_soundEnabled.value) return
        soundScope.launch {
            playSynthesizedTone(587.33, 40, 0.8f) // D5
            kotlinx.coroutines.delay(45)
            playSynthesizedTone(880.0, 70, 0.9f) // A5
        }
    }

    fun playDelete() {
        if (!_soundEnabled.value) return
        soundScope.launch {
            playSynthesizedTone(440.0, 35, 0.7f)
            kotlinx.coroutines.delay(40)
            playSynthesizedTone(330.0, 50, 0.7f)
        }
    }

    fun playWarning() {
        if (!_soundEnabled.value) return
        soundScope.launch {
            playSynthesizedTone(350.0, 60, 0.85f)
            kotlinx.coroutines.delay(70)
            playSynthesizedTone(350.0, 60, 0.85f)
        }
    }

    // --- Haptic Feedback ---
    fun performClick() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(14, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(14)
            }
        } catch (_: Exception) {}
    }

    fun performTick() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(8, 80))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(8)
            }
        } catch (_: Exception) {}
    }

    fun performSuccess() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 18, 40, 24)
                val amplitudes = intArrayOf(0, 180, 0, 240)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    fun performWarning() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    // Composite helpers
    fun tap() {
        playClick()
        performClick()
    }

    fun keypadTap() {
        playKeypad()
        performTick()
    }

    fun successAction() {
        playSuccess()
        performSuccess()
    }

    fun deleteAction() {
        playDelete()
        performWarning()
    }

    fun warningAction() {
        playWarning()
        performWarning()
    }

    fun errorAction() {
        playWarning()
        performWarning()
    }

    companion object {
        private const val KEY_SOUND_ENABLED = "key_sound_effects_enabled"
        private const val KEY_HAPTIC_ENABLED = "key_haptic_feedback_enabled"
    }
}
