package com.dayforge.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DailyJournalContent(
    val type: String, // "morning" or "evening"
    val goals: List<String> = emptyList(),
    val gratitude: String = "",
    val accomplishments: String = "",
    val challenges: String = "",
    val lessons: String = "",
    val mood: Int = 3 // 1-5 scale
)

@Serializable
data class TradeJournalContent(
    val sentiment: String = "",
    val strategyAdherence: Int = 5, // 1-10
    val executionQuality: Int = 5,
    val emotionalState: String = "",
    val lessonsLearned: String = ""
)

@Serializable
data class ActionItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

@Serializable
data class WeeklyReviewContent(
    val notes: String = "",
    val actionItems: List<ActionItem> = emptyList()
)
