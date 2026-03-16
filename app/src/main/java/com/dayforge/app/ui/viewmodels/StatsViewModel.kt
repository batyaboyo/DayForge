package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import com.dayforge.app.data.entities.Goal
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.entities.Trade
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import com.dayforge.app.data.entities.JournalEntry
import com.dayforge.app.data.models.WeeklyReviewContent
import com.dayforge.app.data.models.ActionItem
import java.time.format.DateTimeFormatter

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
    private val todayStr = today.format(DateTimeFormatter.ISO_DATE)
    private val lastWeekStr = today.minusDays(7).format(DateTimeFormatter.ISO_DATE)
    private val currentWeekKey = "week-${today.year}-${today.monthValue}-${today.dayOfMonth / 7}" // Simple week key

    private val _selectedPeriod = MutableStateFlow("Daily")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _weeklyReview = MutableStateFlow(WeeklyReviewContent())
    val weeklyReview: StateFlow<WeeklyReviewContent> = _weeklyReview.asStateFlow()

    init {
        loadWeeklyReview()
    }

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

    val recommendation: StateFlow<String> = combine(_selectedPeriod, dailyStats, weeklyStats) { period, daily, weekly ->
        val stats = if (period == "Daily") daily else weekly
        if (period == "Daily") {
            if (stats.completionRate < 0.6f) 
                "Focus is waning. Clear the next Hacking module to maintain your momentum for the YouTube output tomorrow."
            else 
                "Maximum efficiency detected. Your balance across the 3 pillars is optimal. Prepare for deep work session tonight."
        } else {
            if (stats.completionRate < 0.5f)
                "Weekly output is below target. Priority: Increase Hacking lab frequency and ensure YouTube content is edited."
            else
                "Strong weekly showing. Your consistency is building serious momentum. Focus on high-level strategy for next week."
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating strategy...")

    val streak: StateFlow<Int> = repository.getFinishedDates()
        .map { dates -> calculateStreak(dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun loadWeeklyReview() {
        viewModelScope.launch {
            val entry = repository.getJournal(currentWeekKey, "weekly_review")
            if (entry != null) {
                try {
                    _weeklyReview.value = Json.decodeFromString(entry.contentJson)
                } catch (e: Exception) {
                    // Fallback for malformed data
                }
            } else {
                // Initialize default items if none exist
                _weeklyReview.value = WeeklyReviewContent(
                    actionItems = listOf(
                        ActionItem("1", "Complete 3 Pentesting Labs"),
                        ActionItem("2", "Film & Edit 2 YouTube Videos"),
                        ActionItem("3", "Review 5 Paper Trades"),
                        ActionItem("4", "Document 1 New Hacking Technique")
                    )
                )
                saveWeeklyReview()
            }
        }
    }

    fun updateWeeklyNotes(notes: String) {
        _weeklyReview.value = _weeklyReview.value.copy(notes = notes)
        saveWeeklyReview()
    }

    fun toggleActionItem(itemId: String) {
        val updatedItems = _weeklyReview.value.actionItems.map {
            if (it.id == itemId) it.copy(isCompleted = !it.isCompleted) else it
        }
        _weeklyReview.value = _weeklyReview.value.copy(actionItems = updatedItems)
        saveWeeklyReview()
    }

    private fun saveWeeklyReview() {
        viewModelScope.launch {
            val json = Json.encodeToString(_weeklyReview.value)
            repository.saveJournal(JournalEntry(currentWeekKey, "weekly_review", json))
        }
    }

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        val sortedDates = dates.map { LocalDate.parse(it) }.sortedDescending()
        var currentStreak = 0
        var checkDate = LocalDate.now()
        
        // If today is not in the list, check if yesterday is (streak can stay 
        // if user hasn't finished today YET, but let's be strict: streak is finished days)
        // Actually, common streak logic: if yesterday was finished, and today is ongoing, streak is still there.
        
        if (!sortedDates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }
        
        for (date in sortedDates) {
            if (date == checkDate) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else if (date.isBefore(checkDate)) {
                break
            }
        }
        return currentStreak
    }

    private fun calculateStats(schedule: List<ScheduleBlock>, trades: List<Trade>, goals: List<Goal>): ForgeStats {
        val finished = schedule.count { it.status == "finished" }
        val rate = if (schedule.isNotEmpty()) finished.toFloat() / schedule.size else 0f
        
        return ForgeStats(
            totalBlocks = schedule.size,
            completedBlocks = finished,
            tradesLogged = trades.size,
            studyHours = schedule.filter { (it.category == "study" || it.category == "hacking" || it.category == "projects") && it.status == "finished" }.size * 2,
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
