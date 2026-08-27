package com.example.util

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.MessageDigest

class SecurityManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("portfolio_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_LOCK_ENABLED = "lock_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    var isPasscodeEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOCK_ENABLED, false) && hasPin()
        set(value) = prefs.edit().putBoolean(KEY_LOCK_ENABLED, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    fun hasPin(): Boolean {
        return !prefs.getString(KEY_PIN_HASH, null).isNullOrEmpty()
    }

    fun setPin(pin: String) {
        val hash = hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putBoolean(KEY_LOCK_ENABLED, true)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val savedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return savedHash == hashPin(pin)
    }

    fun disableLock() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .apply()
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // User canceled or pressed negative button
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
