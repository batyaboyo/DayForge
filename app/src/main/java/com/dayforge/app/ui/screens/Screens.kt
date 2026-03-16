package com.dayforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
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
                ScheduleBlockItem(
                    block = block,
                    onClick = {
                        when (block.id) {
                            "morning-journal" -> showMorningJournal = true
                            "evening-journal" -> showEveningJournal = true
                            "trading-scan" -> showTradeLog = true
                            else -> selectedBlock = block
                        }
                    },
                    onToggleFinished = { dailyViewModel.toggleBlockFinished(block) },
                    onToggleSkipped = { dailyViewModel.toggleBlockSkipped(block) }
                )
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
fun ScheduleBlockItem(
    block: ScheduleBlock, 
    onClick: () -> Unit,
    onToggleFinished: () -> Unit,
    onToggleSkipped: () -> Unit
) {
    val alpha = if (block.status == "skipped") 0.6f else 1f
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(if (block.status == "finished") 2.dp else 0.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
        border = if (block.status == "finished") androidx.compose.foundation.BorderStroke(1.dp, StatusFinished.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp).alpha(alpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusIndicator(block.status)
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (block.status == "finished") StatusFinished else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = block.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onToggleSkipped) {
                    Icon(
                        if (block.status == "skipped") Icons.Filled.Cancel else Icons.Outlined.Cancel,
                        contentDescription = "Skip",
                        modifier = Modifier.size(20.dp),
                        tint = if (block.status == "skipped") StatusSkipped else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onToggleFinished) {
                    Icon(
                        if (block.status == "finished") Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Finish",
                        modifier = Modifier.size(20.dp),
                        tint = if (block.status == "finished") StatusFinished else MaterialTheme.colorScheme.outline
                    )
                }
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
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    val goalsViewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))
    
    val stats by viewModel.dailyStats.collectAsState()
    val goals by goalsViewModel.goals.collectAsState()

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
        
        val items = listOf(
            "Complete 3 Pentesting Labs" to true,
            "Film & Edit 2 YouTube Videos" to false,
            "Review 5 Paper Trades" to true,
            "Document 1 New Hacking Technique" to false
        )
        
        items.forEach { (item, completed) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = completed, onCheckedChange = {})
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Forge Notes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Document your wins and lessons for the week...") },
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ReviewPillarCard(goal: Goal) {
    val color = when (goal.category) {
        "hacking" -> Color(0xFF00FF41)
        "youtube" -> Color(0xFFFF0000)
        "trading" -> Color(0xFF00C8FF)
        else -> MaterialTheme.colorScheme.primary
    }
    
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
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun SummaryScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    
    val stats = if (selectedPeriod == "Daily") dailyStats else weeklyStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Command Center", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                Text("Performance metrics.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
            }
            PeriodSelector(selectedPeriod) { viewModel.setPeriod(it) }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Progress Ring Section
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ForgeProgressRing(progress = stats.completionRate)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(stats.completionRate * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "FORGED",
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text("Pillar Performance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Hacking Labs", "${stats.completedBlocks}/${stats.totalBlocks}", Icons.Default.Build, Modifier.weight(1f))
            StatCard("Trading", "${stats.tradesLogged} Trades", Icons.Default.TrendingUp, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Study Time", "${stats.studyHours}h", Icons.Default.Star, Modifier.weight(1f))
            StatCard("Daily Streak", "12 Days", Icons.Default.TrendingUp, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        Text("AI Forge-Sight", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        RecommendationCard(
            if (selectedPeriod == "Daily") {
                if (stats.completionRate < 0.6f) 
                    "Focus is waning. Clear the next Hacking module to maintain your momentum for the YouTube output tomorrow."
                else 
                    "Maximum efficiency detected. Your balance across the 3 pillars is optimal. Prepare for deep work session tonight."
            } else {
                if (stats.completionRate < 0.5f)
                    "Weekly output is below target. Priority: Increase Hacking lab frequency and ensure YouTube content is edited."
                else
                    "Strong weekly showing. Your consistency is building serious momentum. Focus on high-level strategy for next week."
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PeriodSelector(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        listOf("Daily", "Weekly").forEach { period ->
            val isSelected = selected == period
            Surface(
                onClick = { onSelect(period) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(period, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ForgeProgressRing(progress: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    
    Canvas(modifier = Modifier.size(220.dp)) {
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun RecommendationCard(text: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Default.Star, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge, 
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}


