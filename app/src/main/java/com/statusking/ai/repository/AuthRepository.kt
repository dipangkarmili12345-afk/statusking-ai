package com.statusking.ai.repository

import com.statusking.ai.data.local.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val uid: String = "guest_user_1",
    val displayName: String = "Guest Creator",
    val email: String? = null,
    val avatarEmoji: String = "👑",
    val isAnonymous: Boolean = true
)

interface AuthRepository {
    val currentUser: Flow<UserProfile>
    suspend fun signInAsGuest(): UserProfile
    suspend fun updateProfile(name: String, avatar: String)
    suspend fun signOut()
}

class DefaultAuthRepository(private val preferencesManager: PreferencesManager) : AuthRepository {

    private val _currentUser = MutableStateFlow(
        UserProfile(
            displayName = preferencesManager.guestName.value,
            avatarEmoji = preferencesManager.guestAvatar.value
        )
    )
    override val currentUser: Flow<UserProfile> = _currentUser.asStateFlow()

    override suspend fun signInAsGuest(): UserProfile {
        val user = UserProfile(
            displayName = preferencesManager.guestName.value,
            avatarEmoji = preferencesManager.guestAvatar.value
        )
        _currentUser.value = user
        return user
    }

    override suspend fun updateProfile(name: String, avatar: String) {
        preferencesManager.updateGuestProfile(name, avatar)
        _currentUser.value = _currentUser.value.copy(displayName = name, avatarEmoji = avatar)
    }

    override suspend fun signOut() {
        // Reset or prepare for future Firebase Auth sign out
        _currentUser.value = UserProfile(displayName = "Guest Creator", avatarEmoji = "👑")
    }
}
