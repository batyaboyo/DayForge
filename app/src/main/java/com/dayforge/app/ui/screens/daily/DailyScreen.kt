package com.dayforge.app.ui.screens.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayforge.app.DayForgeApplication
import com.dayforge.app.data.entities.ScheduleBlock
import com.dayforge.app.ui.components.StatusIndicator
import com.dayforge.app.ui.theme.*
import com.dayforge.app.ui.viewmodels.*
import com.dayforge.app.ui.screens.MorningJournalDialog
import com.dayforge.app.ui.screens.EveningJournalDialog
import com.dayforge.app.ui.screens.TradeLogDialog
import java.time.format.DateTimeFormatter

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
