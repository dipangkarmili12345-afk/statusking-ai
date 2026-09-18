package com.statusking.ai.data.local

import android.content.Context
import android.content.SharedPreferences
import com.statusking.ai.model.StatusLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(
        StatusLanguage.fromString(prefs.getString(KEY_APP_LANGUAGE, StatusLanguage.HINGLISH.name))
    )
    val appLanguage: StateFlow<StatusLanguage> = _appLanguage.asStateFlow()

    private val _isPremium = MutableStateFlow(prefs.getBoolean(KEY_IS_PREMIUM, false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _guestName = MutableStateFlow(prefs.getString(KEY_GUEST_NAME, "Guest Creator") ?: "Guest Creator")
    val guestName: StateFlow<String> = _guestName.asStateFlow()

    private val _guestAvatar = MutableStateFlow(prefs.getString(KEY_GUEST_AVATAR, "👑") ?: "👑")
    val guestAvatar: StateFlow<String> = _guestAvatar.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _autoSaveEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SAVE, true))
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()

    private val _freeGenerationsLeft = MutableStateFlow(prefs.getInt(KEY_FREE_GENERATIONS, 10))
    val freeGenerationsLeft: StateFlow<Int> = _freeGenerationsLeft.asStateFlow()

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun setAppLanguage(language: StatusLanguage) {
        prefs.edit().putString(KEY_APP_LANGUAGE, language.name).apply()
        _appLanguage.value = language
    }

    fun setPremium(premium: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PREMIUM, premium).apply()
        _isPremium.value = premium
    }

    fun updateGuestProfile(name: String, avatar: String) {
        prefs.edit()
            .putString(KEY_GUEST_NAME, name)
            .putString(KEY_GUEST_AVATAR, avatar)
            .apply()
        _guestName.value = name
        _guestAvatar.value = avatar
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setAutoSaveEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SAVE, enabled).apply()
        _autoSaveEnabled.value = enabled
    }

    fun decrementGeneration(): Boolean {
        val current = _freeGenerationsLeft.value
        if (_isPremium.value) return true
        if (current > 0) {
            val updated = current - 1
            prefs.edit().putInt(KEY_FREE_GENERATIONS, updated).apply()
            _freeGenerationsLeft.value = updated
            return true
        }
        return false
    }

    fun addRewardedGenerations(amount: Int = 5) {
        val updated = _freeGenerationsLeft.value + amount
        prefs.edit().putInt(KEY_FREE_GENERATIONS, updated).apply()
        _freeGenerationsLeft.value = updated
    }

    companion object {
        private const val PREFS_NAME = "statusking_preferences"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"
        private const val KEY_IS_PREMIUM = "key_is_premium"
        private const val KEY_GUEST_NAME = "key_guest_name"
        private const val KEY_GUEST_AVATAR = "key_guest_avatar"
        private const val KEY_NOTIFICATIONS = "key_notifications"
        private const val KEY_AUTO_SAVE = "key_auto_save"
        private const val KEY_FREE_GENERATIONS = "key_free_generations"
    }
}
