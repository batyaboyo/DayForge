package com.dayforge.app.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ForgeCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val label: String
) {
    object Hacking : ForgeCategory("hacking", Icons.Default.Build, Color(0xFF00FF41), "Hacking Labs")
    object YouTube : ForgeCategory("youtube", Icons.Default.PlayArrow, Color(0xFFFF0000), "YouTube Automation")
    object Trading : ForgeCategory("trading", Icons.Default.TrendingUp, Color(0xFF00C8FF), "Trading")
    object Study : ForgeCategory("study", Icons.Default.Star, Color(0xFFFFC107), "Study Time")
    object Fitness : ForgeCategory("fitness", Icons.Default.FitnessCenter, Color(0xFF4CAF50), "Workout")
    object Spiritual : ForgeCategory("spiritual", Icons.Default.SelfImprovement, Color(0xFF9C27B0), "Prayer / Meditation")
    object Meals : ForgeCategory("meals", Icons.Default.Restaurant, Color(0xFFFF5722), "Meals")
    object Leisure : ForgeCategory("leisure", Icons.Default.Celebration, Color(0xFF03A9F4), "Leisure")
    object Sleep : ForgeCategory("sleep", Icons.Default.Bedtime, Color(0xFF607D8B), "Sleep")
    object Journal : ForgeCategory("journal", Icons.Default.EditNote, Color(0xFF795548), "Journaling")
    object Reflection : ForgeCategory("reflection", Icons.Default.Psychology, Color(0xFF009688), "Reflection")
    object Projects : ForgeCategory("projects", Icons.Default.Assignment, Color(0xFF673AB7), "Projects")
    object Wake : ForgeCategory("wake", Icons.Default.WbSunny, Color(0xFFFF9800), "Wake Up")

    companion object {
        fun fromString(value: String): ForgeCategory = when (value.lowercase()) {
            "hacking" -> Hacking
            "youtube" -> YouTube
            "trading" -> Trading
            "study" -> Study
            "fitness" -> Fitness
            "spiritual" -> Spiritual
            "meals" -> Meals
            "leisure" -> Leisure
            "sleep" -> Sleep
            "journal" -> Journal
            "reflection" -> Reflection
            "projects" -> Projects
            "wake" -> Wake
            else -> Study
        }
    }
}
