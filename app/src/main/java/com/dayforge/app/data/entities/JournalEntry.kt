package com.dayforge.app.data.entities

import androidx.room.Entity

@Entity(tableName = "journal_entries", primaryKeys = ["date", "type"])
data class JournalEntry(
    val date: String, // YYYY-MM-DD
    val type: String, // "daily", "trading", "weekly_review"
    val contentJson: String, // Store as JSON for flexibility, or expand fields
    val updatedAt: Long = System.currentTimeMillis()
)
