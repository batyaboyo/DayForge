package com.dayforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.data.entities.Goal
import com.dayforge.app.ui.theme.*
import java.time.format.DateTimeFormatter
import com.dayforge.app.ui.viewmodels.*
@OptIn(ExperimentalLayoutApi::class)

@Composable
fun DailyScreen() {
    val context = LocalContext.current
    val app = (context.applicationContext as DayForgeApplication)
    val dailyViewModel: DailyViewModel = viewModel(factory = DailyViewModelFactory(app.repository))
    val journalViewModel: JournalViewModel = viewModel(factory = JournalViewModelFactory(app.repository))
    
    val schedule by dailyViewModel.schedule.collectAsState()
    val selectedDate by dailyViewModel.selectedDate.collectAsState()
    
    var selectedBlock by remember { mutableStateOf<ScheduleBlock?>(null) }
    var showMorningJournal by remember { mutableStateOf(false) }
    var showEveningJournal by remember { mutableStateOf(false) }
    var showTradeLog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        DateNavigator(
            dateText = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
            onPrevious = { dailyViewModel.navigateDate(-1) },
            onNext = { dailyViewModel.navigateDate(1) },
            onToday = { dailyViewModel.setDate(java.time.LocalDate.now()) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(schedule) { block ->
                ScheduleBlockItem(block) {
                    when (block.id) {
                        "morning-journal" -> showMorningJournal = true
                        "evening-journal" -> showEveningJournal = true
                        "trading-scan" -> showTradeLog = true
                        else -> selectedBlock = block
                    }
                }
            }
        }
    }

    if (showMorningJournal) {
        MorningJournalDialog(
            onDismiss = { showMorningJournal = false },
            onSave = { content ->
                journalViewModel.saveDailyJournal(selectedDate.format(DateTimeFormatter.ISO_DATE), content)
                dailyViewModel.updateBlockStatus(schedule.first { it.id == "morning-journal" }, "finished")
                showMorningJournal = false
            }
        )
    }

    if (showEveningJournal) {
        EveningJournalDialog(
            onDismiss = { showEveningJournal = false },
            onSave = { content ->
                journalViewModel.saveDailyJournal(selectedDate.format(DateTimeFormatter.ISO_DATE), content)
                dailyViewModel.updateBlockStatus(schedule.first { it.id == "evening-journal" }, "finished")
                showEveningJournal = false
            }
        )
    }

    if (showTradeLog) {
        TradeLogDialog(
            onDismiss = { showTradeLog = false },
            onSave = { trade ->
                journalViewModel.addTrade(trade)
                dailyViewModel.updateBlockStatus(schedule.first { it.id == "trading-scan" }, "finished")
                showTradeLog = false
            }
        )
    }

    selectedBlock?.let { block ->
        ScheduleBlockDetailDialog(
            block = block,
            onDismiss = { selectedBlock = null },
            onStatusChange = { newStatus ->
                dailyViewModel.updateBlockStatus(block, newStatus)
                selectedBlock = null
            }
        )
    }
}

@Composable
fun ScheduleBlockDetailDialog(
    block: ScheduleBlock,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(block.title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(block.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(block.purpose, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Update Status", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                
                StatusOptions(currentStatus = block.status, onStatusSelect = onStatusChange)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusOptions(currentStatus: String, onStatusSelect: (String) -> Unit) {
    val statuses = listOf(
        "not-started" to "Not Started",
        "in-progress" to "In Progress",
        "finished" to "Finished",
        "skipped" to "Skipped"
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { (status, label) ->
            FilterChip(
                selected = currentStatus == status,
                onClick = { onStatusSelect(status) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun DateNavigator(
    dateText: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Day")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onToday() }
        ) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap for Today",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        IconButton(onClick = onNext) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Day")
        }
    }
}

@Composable
fun ScheduleBlockItem(block: ScheduleBlock, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusIndicator(block.status)
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = block.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (block.status == "finished") {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusFinished,
                    modifier = Modifier.size(24.dp)
                )
            } else if (block.status == "skipped") {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    tint = StatusSkipped,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val color = when (status) {
        "finished" -> StatusFinished
        "in-progress" -> StatusInProgress
        "skipped" -> StatusSkipped
        else -> StatusNotStarted
    }
    
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

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
    var tempStatus by remember { mutableStateOf(goal.status) }

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
                    val (icon, color) = when (goal.category) {
                        "hacking" -> Icons.Default.Build to Color(0xFF00FF41) // Matrix Green
                        "youtube" -> Icons.Default.PlayArrow to Color(0xFFFF0000) // YouTube Red
                        "trading" -> Icons.Default.TrendingUp to Color(0xFF00C8FF) // Trading Blue
                        else -> Icons.Default.Star to MaterialTheme.colorScheme.primary
                    }
                    Surface(
                        shape = CircleShape,
                        color = color.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
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

@Composable
fun ReviewScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Weekly Review", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Reflect on your performance and align with your 3 pillars.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(24.dp))

        RecommendationCard("Weekly reflection is essential for Ethical Hacking mastery. Review your lab notes and YouTube consistency.")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Weekly Action Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        
        val items = listOf(
            "Complete 3 Pentesting Labs",
            "Film & Edit 2 YouTube Videos",
            "Review 5 Paper Trades",
            "Document 1 New Hacking Technique"
        )
        
        items.forEach { item ->
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text(item, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SummaryScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    
    val stats by viewModel.dailyStats.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daily Summary", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Hacking", "${stats.completedBlocks}/5", Modifier.weight(1f))
            StatCard("YouTube", "Planned", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Trading", "${stats.tradesLogged}", Modifier.weight(1f))
            StatCard("Streak", "5 Days", Modifier.weight(1f)) 
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("AI Recommendation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        RecommendationCard(
            if (stats.completedBlocks < 3) 
                "Lab time is essential. Try to clear at least 2 hacking modules today to stay ahead."
            else 
                "Excellent focus! Your YouTube automation pipeline is looking solid. Remember to document your trading lessons tonight."
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun RecommendationCard(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Star, contentDescription = "AI", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}


