package com.dayforge.app.data.repository

import com.dayforge.app.data.dao.DayForgeDao
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.entities.Trade
import com.dayforge.app.data.entities.JournalEntry
import com.dayforge.app.data.entities.Goal
import kotlinx.coroutines.flow.Flow

class DayForgeRepository(private val dao: DayForgeDao) {
    // Schedule
    fun getScheduleForDate(date: String): Flow<List<ScheduleBlock>> = dao.getScheduleForDate(date)
    
    fun getScheduleForRange(startDate: String, endDate: String): Flow<List<ScheduleBlock>> = dao.getScheduleForRange(startDate, endDate)
    
    suspend fun saveSchedule(blocks: List<ScheduleBlock>) = dao.insertScheduleBlocks(blocks)
    
    suspend fun updateBlock(block: ScheduleBlock) = dao.updateScheduleBlock(block)

    fun getFinishedDates(): Flow<List<String>> = dao.getFinishedDates()

    // Trades
    fun getTradesForDate(date: String): Flow<List<Trade>> = dao.getTradesForDate(date)
    
    fun getTradesForRange(startDate: String, endDate: String): Flow<List<Trade>> = dao.getTradesForRange(startDate, endDate)
    
    suspend fun addTrade(trade: Trade) = dao.insertTrade(trade)
    
    suspend fun updateTrade(trade: Trade) = dao.updateTrade(trade)
    
    suspend fun deleteTrade(trade: Trade) = dao.deleteTrade(trade)

    // Journals
    suspend fun getJournal(date: String, type: String): JournalEntry? = dao.getJournalEntry(date, type)
    
    suspend fun saveJournal(entry: JournalEntry) = dao.insertJournalEntry(entry)

    // Goals
    fun getAllGoals(): Flow<List<Goal>> = dao.getAllGoals()
    
    suspend fun saveGoal(goal: Goal) = dao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = dao.updateGoal(goal)
}
