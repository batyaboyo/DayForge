package com.dayforge.app.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayforge.app.DayForgeApplication
import com.dayforge.app.data.entities.Goal
import com.dayforge.app.ui.theme.*
import com.dayforge.app.ui.viewmodels.*
import com.dayforge.app.data.models.ForgeCategory

@Composable
fun ReviewScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    val goalsViewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))
    
    val stats by viewModel.dailyStats.collectAsState()
    val goals by goalsViewModel.goals.collectAsState()
    val review by viewModel.weeklyReview.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Weekly Review", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
        Text("Pillar alignment and growth reflection.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(32.dp))

        Text("Pillar Mastery", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        goals.take(3).forEach { goal ->
            ReviewPillarCard(goal)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Mastery Action Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        review.actionItems.forEach { item ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = item.isCompleted, onCheckedChange = { viewModel.toggleActionItem(item.id) })
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Forge Notes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = review.notes,
            onValueChange = { viewModel.updateWeeklyNotes(it) },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Document your wins and lessons for the week...") },
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
@Composable
fun ReviewPillarCard(goal: Goal) {
    val category = ForgeCategory.fromString(goal.category)
    val color = category.color
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(12.dp))
                Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = goal.progress,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}
