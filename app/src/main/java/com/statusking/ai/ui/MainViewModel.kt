package com.statusking.ai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.statusking.ai.data.local.PreferencesManager
import com.statusking.ai.model.StatusItem
import com.statusking.ai.repository.AiRepository
import com.statusking.ai.repository.AuthRepository
import com.statusking.ai.repository.StatusRepository
import com.statusking.ai.repository.UserProfile
import com.statusking.ai.service.AdsManager
import com.statusking.ai.service.BillingManager
import com.statusking.ai.service.ExportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    val statusRepository: StatusRepository,
    val aiRepository: AiRepository,
    val authRepository: AuthRepository,
    val billingManager: BillingManager,
    val adsManager: AdsManager,
    val exportManager: ExportManager,
    val preferencesManager: PreferencesManager
) : ViewModel() {

    val isPremium: StateFlow<Boolean> = preferencesManager.isPremium
    val themeMode: StateFlow<String> = preferencesManager.themeMode

    val currentUser: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val favorites: StateFlow<List<StatusItem>> = statusRepository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creations: StateFlow<List<StatusItem>> = statusRepository.allCreations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStatuses: StateFlow<List<StatusItem>> = statusRepository.allStatuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingStatus = MutableStateFlow<StatusItem?>(null)
    val editingStatus: StateFlow<StatusItem?> = _editingStatus.asStateFlow()

    init {
        viewModelScope.launch {
            statusRepository.preloadIfEmpty()
        }
    }

    fun setEditingStatus(status: StatusItem?) {
        _editingStatus.value = status
    }

    fun toggleFavorite(status: StatusItem) {
        viewModelScope.launch {
            statusRepository.toggleFavorite(status)
        }
    }
}

class MainViewModelFactory(
    private val statusRepository: StatusRepository,
    private val aiRepository: AiRepository,
    private val authRepository: AuthRepository,
    private val billingManager: BillingManager,
    private val adsManager: AdsManager,
    private val exportManager: ExportManager,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                statusRepository,
                aiRepository,
                authRepository,
                billingManager,
                adsManager,
                exportManager,
                preferencesManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
