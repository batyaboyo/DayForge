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
import com.dayforge.app.data.entities.ScheduleBlock
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
                Text(
                    text = "✓",
                    color = StatusFinished,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Goals View - Coming Soon")
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
            StatCard("Completion", "${(stats.completedBlocks.toFloat() / stats.totalBlocks.coerceAtLeast(1) * 100).toInt()}%", Modifier.weight(1f))
            StatCard("Study", "${stats.studyHours}h", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Trades", "${stats.tradesLogged}", Modifier.weight(1f))
            StatCard("Streak", "5 Days", Modifier.weight(1f)) // Hardcoded for now
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("AI Recommendation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        RecommendationCard(
            if (stats.completedBlocks < 5) 
                "You're falling behind on your schedule. Try to finish the 'Deep Study' block to maintain momentum."
            else 
                "Great consistency today! You're on track to hit your weekly learning goals. Consider a lighter 'Reflection' evening."
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

@Composable
fun ReviewScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Review View - Coming Soon")
    }
}
