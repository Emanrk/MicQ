package com.eman.micq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eman.micq.data.model.DjShift
import com.eman.micq.data.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ShiftHistoryState {
    object Loading : ShiftHistoryState()
    data class Success(val shifts: List<DjShift>) : ShiftHistoryState()
    data class Error(val message: String) : ShiftHistoryState()
}

@HiltViewModel
class ShiftHistoryViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShiftHistoryState>(ShiftHistoryState.Loading)
    val uiState: StateFlow<ShiftHistoryState> = _uiState.asStateFlow()

    init {
        loadShifts()
    }

    private fun loadShifts() {
        viewModelScope.launch {
            _uiState.value = ShiftHistoryState.Loading
            shiftRepository.getAllShifts()
                .onSuccess { shifts ->
                    _uiState.value = ShiftHistoryState.Success(shifts)
                }
                .onFailure { error ->
                    _uiState.value = ShiftHistoryState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
