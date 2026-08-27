package com.example.util

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    fun getLanguage(): AppLanguage {
        val code = prefs.getString(KEY_LANGUAGE, "fa") ?: "fa"
        return if (code == "en") AppLanguage.ENGLISH else AppLanguage.PERSIAN
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return try {
            AppThemeMode.valueOf(name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    fun setThemeMode(theme: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, "تومان") ?: "تومان"
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getUsePersianDigits(): Boolean {
        return prefs.getBoolean(KEY_PERSIAN_DIGITS, false)
    }

    fun setUsePersianDigits(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PERSIAN_DIGITS, enabled).apply()
    }

    fun getTolerance(): Double {
        return prefs.getFloat(KEY_TOLERANCE, 0.2f).toDouble()
    }

    fun setTolerance(tolerance: Double) {
        prefs.edit().putFloat(KEY_TOLERANCE, tolerance.toFloat()).apply()
    }

    fun getActivePortfolioId(): Int {
        return prefs.getInt(KEY_ACTIVE_PORTFOLIO_ID, 1)
    }

    fun setActivePortfolioId(id: Int) {
        prefs.edit().putInt(KEY_ACTIVE_PORTFOLIO_ID, id).apply()
    }

    fun getPrivacyMode(): Boolean {
        return prefs.getBoolean(KEY_PRIVACY_MODE, false)
    }

    fun setPrivacyMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRIVACY_MODE, enabled).apply()
    }

    fun resetSettingsToDefaults() {
        prefs.edit()
            .putString(KEY_LANGUAGE, "fa")
            .putString(KEY_THEME, AppThemeMode.SYSTEM.name)
            .putString(KEY_CURRENCY, "تومان")
            .putBoolean(KEY_PERSIAN_DIGITS, false)
            .putFloat(KEY_TOLERANCE, 0.2f)
            .putBoolean(KEY_PRIVACY_MODE, false)
            .apply()
    }

    companion object {
        private const val KEY_LANGUAGE = "setting_language"
        private const val KEY_THEME = "setting_theme"
        private const val KEY_CURRENCY = "setting_currency"
        private const val KEY_PERSIAN_DIGITS = "setting_persian_digits"
        private const val KEY_TOLERANCE = "setting_tolerance"
        private const val KEY_ACTIVE_PORTFOLIO_ID = "setting_active_portfolio_id"
        private const val KEY_PRIVACY_MODE = "setting_privacy_mode"
    }
}
