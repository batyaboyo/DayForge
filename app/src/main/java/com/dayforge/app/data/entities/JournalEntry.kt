package com.dayforge.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val type: String, // "daily" or "trading"
    val contentJson: String, // Store as JSON for flexibility, or expand fields
    val updatedAt: Long = System.currentTimeMillis()
)
