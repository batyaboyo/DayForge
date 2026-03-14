package com.dayforge.app.ui.viewmodels

import androidx.lifecycle.*
import com.dayforge.app.data.entities.JournalEntry
import com.dayforge.app.data.entities.Trade
import com.dayforge.app.data.models.DailyJournalContent
import com.dayforge.app.data.models.TradeJournalContent
import com.dayforge.app.data.repository.DayForgeRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JournalViewModel(private val repository: DayForgeRepository) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

    fun saveDailyJournal(date: String, content: DailyJournalContent) {
        viewModelScope.launch {
            val entry = JournalEntry(
                date = date,
                type = "daily",
                contentJson = json.encodeToString(content)
            )
            repository.saveJournal(entry)
        }
    }

    suspend fun getDailyJournal(date: String): DailyJournalContent? {
        val entry = repository.getJournal(date, "daily")
        return entry?.let { json.decodeFromString<DailyJournalContent>(it.contentJson) }
    }

    fun addTrade(trade: Trade) {
        viewModelScope.launch {
            repository.addTrade(trade)
        }
    }

    fun getTradesForDate(date: String) = repository.getTradesForDate(date)
}

class JournalViewModelFactory(private val repository: DayForgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
