package com.dayforge.app.data.entities

import androidx.room.*

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "hacking", "youtube", "trading"
    val progress: Float = 0f, // 0.0 to 1.0
    val status: String = "In Progress",
    val notes: String = "",
    val isFinished: Boolean = false,
    val isSkipped: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
