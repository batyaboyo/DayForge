package com.dayforge.app.data.dao

import androidx.room.*
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.entities.Trade
import com.dayforge.app.data.entities.JournalEntry
import com.dayforge.app.data.entities.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface DayForgeDao {
    // Schedule
    @Query("SELECT * FROM schedule_blocks WHERE date = :date")
    fun getScheduleForDate(date: String): Flow<List<ScheduleBlock>>

    @Query("SELECT * FROM schedule_blocks WHERE date BETWEEN :startDate AND :endDate")
    fun getScheduleForRange(startDate: String, endDate: String): Flow<List<ScheduleBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleBlocks(blocks: List<ScheduleBlock>)

    @Update
    suspend fun updateScheduleBlock(block: ScheduleBlock)

    // Trades
    @Query("SELECT * FROM trades WHERE date = :date")
    fun getTradesForDate(date: String): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE date BETWEEN :startDate AND :endDate")
    fun getTradesForRange(startDate: String, endDate: String): Flow<List<Trade>>

    @Insert
    suspend fun insertTrade(trade: Trade)

    @Update
    suspend fun updateTrade(trade: Trade)

    @Delete
    suspend fun deleteTrade(trade: Trade)

    // Journals
    @Query("SELECT * FROM journal_entries WHERE date = :date AND type = :type")
    suspend fun getJournalEntry(date: String, type: String): JournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    // Goals
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)
}
