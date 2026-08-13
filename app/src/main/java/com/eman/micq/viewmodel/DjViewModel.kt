package com.eman.micq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eman.micq.data.model.DjShift
import com.eman.micq.data.repository.AuthRepository
import com.eman.micq.data.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DjUiState(
    val activeShift: DjShift? = null,
    val isLoading: Boolean = false,
    val elapsedTime: String = "00:00:00"
)

@HiltViewModel
class DjViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DjUiState())
    val uiState: StateFlow<DjUiState> = _uiState.asStateFlow()

    init {
        checkActiveShift()
    }

    private fun checkActiveShift() {
        val dj = authRepository.currentUser ?: return
        viewModelScope.launch {
            shiftRepository.getActiveShift(dj.uid).onSuccess { shift ->
                _uiState.value = _uiState.value.copy(activeShift = shift)
                if (shift != null) startTimer()
            }
        }
    }

    fun startShift() {
        val dj = authRepository.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            shiftRepository.startShift(dj.uid, dj.displayName).onSuccess { shift ->
                _uiState.value = _uiState.value.copy(activeShift = shift, isLoading = false)
                startTimer()
            }
        }
    }

    fun endShift() {
        val shiftId = _uiState.value.activeShift?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            shiftRepository.endShift(shiftId).onSuccess {
                _uiState.value = _uiState.value.copy(activeShift = null, isLoading = false, elapsedTime = "00:00:00")
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.activeShift != null) {
                val start = _uiState.value.activeShift?.startTime ?: break
                val diff = System.currentTimeMillis() - start
                _uiState.value = _uiState.value.copy(elapsedTime = formatTime(diff))
                delay(1000)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
