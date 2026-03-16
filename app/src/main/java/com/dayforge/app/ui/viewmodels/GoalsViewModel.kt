package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.entities.Goal
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GoalsViewModel(private val repository: DayForgeRepository) : ViewModel() {

    val goals: StateFlow<List<Goal>> = repository.getAllGoals()
        .onEach { list ->
            if (list.isEmpty()) {
                initializeDefaultGoals()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun initializeDefaultGoals() {
        viewModelScope.launch {
            val defaults = listOf(
                Goal("hacking", "Ethical Hacking", "hacking", 0.1f, "Learning Fundamentals", "Focusing on OSCP syllabus and lab work."),
                Goal("youtube", "YouTube Automation", "youtube", 0.05f, "Planning Channels", "Managing 3 automation channels. Content pipeline setup."),
                Goal("trading", "Trading Mastery", "trading", 0.2f, "Strategy Testing", "Refining 1-minute execution strategy.")
            )
            for (goal in defaults) {
                repository.saveGoal(goal)
            }
        }
    }

    fun updateGoalProgress(goal: Goal, progress: Float) {
        viewModelScope.launch {
            repository.saveGoal(goal.copy(progress = progress, lastUpdated = System.currentTimeMillis()))
        }
    }

    fun updateGoalStatus(goal: Goal, status: String) {
        viewModelScope.launch {
            repository.saveGoal(goal.copy(status = status, lastUpdated = System.currentTimeMillis()))
        }
    }

    fun toggleGoalFinished(goal: Goal) {
        viewModelScope.launch {
            repository.saveGoal(goal.copy(
                isFinished = !goal.isFinished,
                isSkipped = false,
                lastUpdated = System.currentTimeMillis()
            ))
        }
    }

    fun toggleGoalSkipped(goal: Goal) {
        viewModelScope.launch {
            repository.saveGoal(goal.copy(
                isSkipped = !goal.isSkipped,
                isFinished = false,
                lastUpdated = System.currentTimeMillis()
            ))
        }
    }
}

class GoalsViewModelFactory(private val repository: DayForgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
