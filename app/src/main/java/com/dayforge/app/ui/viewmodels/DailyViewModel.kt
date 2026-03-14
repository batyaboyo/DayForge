package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyViewModel(private val repository: DayForgeRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val schedule: StateFlow<List<ScheduleBlock>> = _selectedDate
        .flatMapLatest { date ->
            repository.getScheduleForDate(date.format(DateTimeFormatter.ISO_DATE))
        }
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

    private fun initializeDefaultBlocks(date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val defaults = listOf(
                ScheduleBlock("wake", "Wake", "06:00", "Start the day with intention.", "wake", date = dateStr),
                ScheduleBlock("morning-journal", "Morning Journal", "06:30", "Set intentions.", "journal", date = dateStr),
                ScheduleBlock("prayer", "Prayer / Meditation", "06:45", "Center yourself.", "spiritual", date = dateStr),
                ScheduleBlock("workout", "Workout", "07:00", "Build physical strength.", "fitness", date = dateStr),
                ScheduleBlock("breakfast", "Breakfast", "08:00", "Fuel your body.", "meals", date = dateStr),
                ScheduleBlock("deep-study-1", "Deep Study 1", "09:00", "Cybersecurity labs.", "study", date = dateStr),
                ScheduleBlock("lunch", "Lunch", "12:00", "Nourish and recharge.", "meals", date = dateStr),
                ScheduleBlock("deep-study-2", "Deep Study 2", "13:00", "Blockchain development.", "study", date = dateStr),
                ScheduleBlock("projects", "Projects / Labs", "16:00", "Apply knowledge.", "projects", date = dateStr),
                ScheduleBlock("trading-scan", "Trading Scan", "17:30", "Analyze markets.", "trading", date = dateStr),
                ScheduleBlock("dinner", "Dinner", "18:30", "Quality meal.", "meals", date = dateStr),
                ScheduleBlock("walk", "Walk", "19:30", "Movement and fresh air.", "leisure", date = dateStr),
                ScheduleBlock("trading-review", "Trading Review", "20:00", "Document lessons.", "trading", date = dateStr),
                ScheduleBlock("reading", "Reading", "20:30", "Expand knowledge.", "leisure", date = dateStr),
                ScheduleBlock("evening-journal", "Evening Journal", "21:30", "Reflect on the day.", "journal", date = dateStr),
                ScheduleBlock("reflection", "Evening Reflection", "21:45", "Review progress.", "reflection", date = dateStr),
                ScheduleBlock("wind-down", "Wind Down", "22:00", "Prepare for sleep.", "leisure", date = dateStr),
                ScheduleBlock("sleep", "Sleep", "22:30", "Recovery.", "sleep", date = dateStr)
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
