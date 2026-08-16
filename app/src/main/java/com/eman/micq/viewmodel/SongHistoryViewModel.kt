package com.eman.micq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eman.micq.data.model.QueueEntry
import com.eman.micq.data.repository.AuthRepository
import com.eman.micq.data.repository.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SongHistoryState {
    object Loading : SongHistoryState()
    data class Success(val entries: List<QueueEntry>) : SongHistoryState()
    data class Error(val message: String) : SongHistoryState()
}

@HiltViewModel
class SongHistoryViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongHistoryState>(SongHistoryState.Loading)
    val uiState: StateFlow<SongHistoryState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        val user = authRepository.currentUser
        if (user == null) {
            _uiState.value = SongHistoryState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = SongHistoryState.Loading
            
            // Calculate timestamp for 7 days ago
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            
            queueRepository.getUserSongHistory(user.uid, user.role, sevenDaysAgo)
                .onSuccess { entries ->
                    _uiState.value = SongHistoryState.Success(entries)
                }
                .onFailure { error ->
                    _uiState.value = SongHistoryState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
