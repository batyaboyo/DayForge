package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import com.dayforge.app.data.entities.Goal

data class ForgeStats(
    val totalBlocks: Int = 0,
    val completedBlocks: Int = 0,
    val studyHours: Int = 0,
    val tradesLogged: Int = 0,
    val completionRate: Float = 0f,
    val goalPillars: List<Goal> = emptyList()
)

class StatsViewModel(private val repository: DayForgeRepository) : ViewModel() {

    private val today = LocalDate.now()
    private val todayStr = today.format(java.time.format.DateTimeFormatter.ISO_DATE)
    private val lastWeekStr = today.minusDays(7).format(java.time.format.DateTimeFormatter.ISO_DATE)

    private val _selectedPeriod = MutableStateFlow("Daily")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
    }

    val dailyStats: StateFlow<ForgeStats> = combine(
        repository.getScheduleForDate(todayStr),
        repository.getTradesForDate(todayStr),
        repository.getAllGoals()
    ) { schedule, trades, goals ->
        calculateStats(schedule, trades, goals)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ForgeStats())

    val weeklyStats: StateFlow<ForgeStats> = combine(
        repository.getScheduleForRange(lastWeekStr, todayStr),
        repository.getTradesForRange(lastWeekStr, todayStr),
        repository.getAllGoals()
    ) { schedule, trades, goals ->
        calculateStats(schedule, trades, goals)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ForgeStats())

    private fun calculateStats(schedule: List<ScheduleBlock>, trades: List<Trade>, goals: List<Goal>): ForgeStats {
        val finished = schedule.count { it.status == "finished" }
        val rate = if (schedule.isNotEmpty()) finished.toFloat() / schedule.size else 0f
        
        return ForgeStats(
            totalBlocks = schedule.size,
            completedBlocks = finished,
            tradesLogged = trades.size,
            studyHours = schedule.filter { it.category == "study" && it.status == "finished" }.size * 2,
            completionRate = rate,
            goalPillars = goals.take(3)
        )
    }
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
