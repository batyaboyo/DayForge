package com.dayforge.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun GoalsScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))
    
    val goals by viewModel.goals.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your 3 Pillars", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
        Text("Core areas of absolute focus and growth.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    onProgressChange = { viewModel.updateGoalProgress(goal, it) },
                    onStatusChange = { viewModel.updateGoalStatus(goal, it) },
                    onToggleFinished = { viewModel.toggleGoalFinished(goal) },
                    onToggleSkipped = { viewModel.toggleGoalSkipped(goal) }
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal, 
    onProgressChange: (Float) -> Unit, 
    onStatusChange: (String) -> Unit,
    onToggleFinished: () -> Unit,
    onToggleSkipped: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var tempStatus by remember(goal.status) { mutableStateOf(goal.status) }

    val cardAlpha = if (goal.isSkipped) 0.6f else 1f
    
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(if (goal.isFinished) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp)),
        border = if (goal.isFinished) androidx.compose.foundation.BorderStroke(2.dp, StatusFinished) else null
    ) {
        Column(modifier = Modifier.padding(24.dp).alpha(cardAlpha)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val category = ForgeCategory.fromString(goal.category)
                    Surface(
                        shape = CircleShape,
                        color = category.color.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            goal.title, 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Bold,
                            color = if (goal.isFinished) StatusFinished else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            goal.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onToggleSkipped) {
                        Icon(
                            if (goal.isSkipped) Icons.Filled.Cancel else Icons.Outlined.Cancel,
                            contentDescription = "Skip",
                            tint = if (goal.isSkipped) StatusSkipped else MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(onClick = onToggleFinished) {
                        Icon(
                            if (goal.isFinished) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Finish",
                            tint = if (goal.isFinished) StatusFinished else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                goal.notes, 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Progress", style = MaterialTheme.typography.labelMedium)
                        Text("${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { goal.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = if (goal.isFinished) StatusFinished else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { showEditDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = goal.status,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { onProgressChange((goal.progress - 0.05f).coerceIn(0f, 1f)) },
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 20.sp)
                    }
                    TextButton(
                        onClick = { onProgressChange((goal.progress + 0.05f).coerceIn(0f, 1f)) },
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Goal Status") },
            text = {
                OutlinedTextField(
                    value = tempStatus,
                    onValueChange = { tempStatus = it },
                    label = { Text("Current Status/Focus") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onStatusChange(tempStatus)
                    showEditDialog = false
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}
