package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate

data class WeeklyStats(
    val totalBlocks: Int = 0,
    val completedBlocks: Int = 0,
    val studyHours: Int = 0,
    val tradesLogged: Int = 0
)

class StatsViewModel(private val repository: DayForgeRepository) : ViewModel() {

    private val today = LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_DATE)

    val dailyStats: StateFlow<WeeklyStats> = repository.getScheduleForDate(today)
        .combine(repository.getTradesForDate(today)) { schedule, trades ->
            val finished = schedule.count { it.status == "finished" }
            WeeklyStats(
                totalBlocks = schedule.size,
                completedBlocks = finished,
                tradesLogged = trades.size,
                studyHours = schedule.filter { it.category == "study" && it.status == "finished" }.size * 2 // Assuming 2h per block for now
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyStats())
}

class StatsViewModelFactory(private val repository: DayForgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
