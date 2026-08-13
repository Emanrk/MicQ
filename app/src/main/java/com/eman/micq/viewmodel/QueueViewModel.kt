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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QueueUiState(
    val entries: List<QueueEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState: StateFlow<QueueUiState> = _uiState.asStateFlow()

    fun observeQueue(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            queueRepository.getQueueForSession(sessionId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
                .collect { queueEntries ->
                    _uiState.value = _uiState.value.copy(entries = queueEntries, isLoading = false)
                }
        }
    }

    fun addEntry(
        sessionId: String,
        firstName: String,
        lastName: String,
        songName: String,
        tableNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val entry = QueueEntry(
                firstName = firstName,
                lastName = lastName,
                songName = songName,
                tableNumber = tableNumber
            )
            queueRepository.addToQueue(sessionId, entry)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun leaveQueue(sessionId: String, entryId: String) {
        viewModelScope.launch {
            queueRepository.removeFromQueue(sessionId, entryId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun updateEntryStatus(sessionId: String, entryId: String, status: String) {
        viewModelScope.launch {
            queueRepository.updateEntryStatus(sessionId, entryId, status)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }
}
