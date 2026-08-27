package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalSoundHaptic = staticCompositionLocalOf<SoundHapticHelper> {
    error("No SoundHapticHelper provided")
}

class SoundHapticHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_sound_haptic_prefs", Context.MODE_PRIVATE)
    private val audioManager: AudioManager? = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var toneGenerator: ToneGenerator? = null

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC_ENABLED, true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 30)
        } catch (e: Exception) {
            toneGenerator = null
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

    // --- Sound Effects ---
    fun playClick() {
        if (!_soundEnabled.value) return
        try {
            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 0.5f)
        } catch (e: Exception) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
            } catch (_: Exception) {}
        }
    }

    fun playKeypad() {
        if (!_soundEnabled.value) return
        try {
            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 0.6f)
        } catch (e: Exception) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 20)
            } catch (_: Exception) {}
        }
    }

    fun playSuccess() {
        if (!_soundEnabled.value) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
        } catch (e: Exception) {
            try {
                audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 1.0f)
            } catch (_: Exception) {}
        }
    }

    fun playDelete() {
        if (!_soundEnabled.value) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 50)
        } catch (e: Exception) {
            try {
                audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 0.7f)
            } catch (_: Exception) {}
        }
    }

    fun playWarning() {
        if (!_soundEnabled.value) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 60)
        } catch (e: Exception) {
            try {
                audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 0.8f)
            } catch (_: Exception) {}
        }
    }

    // --- Haptic Feedback ---
    fun performClick() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(12)
            }
        } catch (_: Exception) {}
    }

    fun performTick() {
        if (!_hapticEnabled.value || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(8, 70))
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
                val timings = longArrayOf(0, 15, 40, 20)
                val amplitudes = intArrayOf(0, 180, 0, 220)
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
                vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
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

    companion object {
        private const val KEY_SOUND_ENABLED = "key_sound_effects_enabled"
        private const val KEY_HAPTIC_ENABLED = "key_haptic_feedback_enabled"
    }
}
