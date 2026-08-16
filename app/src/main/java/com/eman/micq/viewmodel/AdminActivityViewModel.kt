package com.eman.micq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eman.micq.data.model.DjShift
import com.eman.micq.data.repository.QueueRepository
import com.eman.micq.data.repository.ShiftRepository
import com.eman.micq.data.repository.SingerLoyalty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShiftActivity(
    val shift: DjShift,
    val songCount: Int
)

sealed class AdminActivityState {
    object Loading : AdminActivityState()
    data class Success(
        val shiftActivities: List<ShiftActivity>,
        val loyaltyData: List<SingerLoyalty>
    ) : AdminActivityState()
    data class Error(val message: String) : AdminActivityState()
}

@HiltViewModel
class AdminActivityViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminActivityState>(AdminActivityState.Loading)
    val uiState: StateFlow<AdminActivityState> = _uiState.asStateFlow()

    init {
        loadActivity()
    }

    fun loadActivity() {
        viewModelScope.launch {
            _uiState.value = AdminActivityState.Loading
            
            val shiftsResult = shiftRepository.getAllShifts()
            val loyaltyResult = queueRepository.getLoyaltyData()

            if (shiftsResult.isSuccess && loyaltyResult.isSuccess) {
                val shifts = shiftsResult.getOrThrow()
                val loyalty = loyaltyResult.getOrThrow()

                val activities = shifts.map { shift ->
                    val endTime = shift.endTime ?: System.currentTimeMillis()
                    val count = queueRepository.getSongCountForDj(shift.djId, shift.startTime, endTime)
                        .getOrDefault(0)
                    ShiftActivity(shift, count)
                }
                _uiState.value = AdminActivityState.Success(activities, loyalty)
            } else {
                val error = shiftsResult.exceptionOrNull()?.message ?: loyaltyResult.exceptionOrNull()?.message ?: "Unknown error"
                _uiState.value = AdminActivityState.Error(error)
            }
        }
    }
}
