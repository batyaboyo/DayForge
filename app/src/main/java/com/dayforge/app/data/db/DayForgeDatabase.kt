package com.dayforge.app.data.db

import android.content.Context
import androidx.room.*
import com.dayforge.app.data.dao.DayForgeDao
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.entities.Trade
import com.dayforge.app.data.entities.JournalEntry

@Database(
    entities = [ScheduleBlock::class, Trade::class, JournalEntry::class],
    version = 1,
    exportSchema = false
)
abstract class DayForgeDatabase : RoomDatabase() {
    abstract fun dao(): DayForgeDao

    companion object {
        @Volatile
        private var INSTANCE: DayForgeDatabase? = null

        fun getDatabase(context: Context): DayForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DayForgeDatabase::class.java,
                    "dayforge_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
