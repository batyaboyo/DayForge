package com.dayforge.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val asset: String,
    val bias: String,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val result: String = "pending",
    val notes: String,
    val date: String, // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)
