package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.dayforge.app.data.models.ForgeCategory

class DailyViewModel(private val repository: DayForgeRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val schedule: StateFlow<List<ScheduleBlock>> = _selectedDate
        .flatMapLatest { date ->
            repository.getScheduleForDate(date.format(DateTimeFormatter.ISO_DATE))
        }
        .map { list -> list.sortedBy { it.time } }
        .onEach { list ->
            if (list.isEmpty()) {
                initializeDefaultBlocks(_selectedDate.value)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateDate(days: Int) {
        _selectedDate.value = _selectedDate.value.plusDays(days.toLong())
    }

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateBlockStatus(block: ScheduleBlock, status: String) {
        viewModelScope.launch {
            repository.updateBlock(block.copy(status = status))
        }
    }

    fun toggleBlockFinished(block: ScheduleBlock) {
        viewModelScope.launch {
            val newStatus = if (block.status == "finished") "not-started" else "finished"
            repository.updateBlock(block.copy(status = newStatus))
        }
    }

    fun toggleBlockSkipped(block: ScheduleBlock) {
        viewModelScope.launch {
            val newStatus = if (block.status == "skipped") "not-started" else "skipped"
            repository.updateBlock(block.copy(status = newStatus))
        }
    }

    private fun initializeDefaultBlocks(date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val defaults = listOf(
                ScheduleBlock("${dateStr}_wake", "Wake", "06:00", "Start the day with intention.", ForgeCategory.Wake.name, date = dateStr),
                ScheduleBlock("${dateStr}_prayer", "Prayer / Meditation", "06:15", "Center yourself.", ForgeCategory.Spiritual.name, date = dateStr),
                ScheduleBlock("${dateStr}_workout", "Workout", "06:45", "Build physical strength.", ForgeCategory.Fitness.name, date = dateStr),
                ScheduleBlock("${dateStr}_morning-journal", "Morning Journal", "07:45", "Set intentions.", ForgeCategory.Journal.name, date = dateStr),
                ScheduleBlock("${dateStr}_breakfast", "Breakfast", "08:15", "Fuel your body.", ForgeCategory.Meals.name, date = dateStr),
                ScheduleBlock("${dateStr}_hacking-labs", "Ethical Hacking & Pentesting", "09:00", "Hands-on labs and certifications.", ForgeCategory.Hacking.name, date = dateStr),
                ScheduleBlock("${dateStr}_lunch", "Lunch", "12:00", "Nourish and recharge.", ForgeCategory.Meals.name, date = dateStr),
                ScheduleBlock("${dateStr}_youtube-auto", "YouTube Automation", "13:00", "Planning, editing, and channel management.", ForgeCategory.YouTube.name, date = dateStr),
                ScheduleBlock("${dateStr}_deep-projects", "Deep Work / Projects", "16:00", "3-channel output management.", ForgeCategory.Projects.name, date = dateStr),
                ScheduleBlock("${dateStr}_trading-scan", "Trading Scan", "17:30", "Analyze markets and prep trades.", ForgeCategory.Trading.name, date = dateStr),
                ScheduleBlock("${dateStr}_walk", "Walk", "18:00", "Movement and fresh air.", ForgeCategory.Leisure.name, date = dateStr),
                ScheduleBlock("${dateStr}_dinner", "Dinner", "18:30", "Quality meal.", ForgeCategory.Meals.name, date = dateStr),
                ScheduleBlock("${dateStr}_trading-review", "Trading Review", "19:30", "Document lessons and journal trades.", ForgeCategory.Trading.name, date = dateStr),
                ScheduleBlock("${dateStr}_reading", "Reading", "20:30", "Expand knowledge.", ForgeCategory.Leisure.name, date = dateStr),
                ScheduleBlock("${dateStr}_evening-journal", "Evening Journal", "21:30", "Reflect on accomplishments.", ForgeCategory.Journal.name, date = dateStr),
                ScheduleBlock("${dateStr}_reflection", "Evening Reflection", "21:45", "Review progress.", ForgeCategory.Reflection.name, date = dateStr),
                ScheduleBlock("${dateStr}_wind-down", "Wind Down", "22:00", "Prepare for sleep.", ForgeCategory.Leisure.name, date = dateStr),
                ScheduleBlock("${dateStr}_sleep", "Sleep", "22:30", "Recovery.", ForgeCategory.Sleep.name, date = dateStr)
            )
            repository.saveSchedule(defaults)
        }
    }
}

class DailyViewModelFactory(private val repository: DayForgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
