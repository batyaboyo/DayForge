package com.dayforge.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_blocks")
data class ScheduleBlock(
    @PrimaryKey val id: String,
    val title: String,
    val time: String,
    val purpose: String,
    val category: String,
    val status: String = "not-started",
    val didIt: Boolean = false,
    val finishedIt: Boolean = false,
    val date: String // YYYY-MM-DD
)
