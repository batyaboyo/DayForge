package com.dayforge.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dayforge.app.data.entities.Trade
import com.dayforge.app.data.models.DailyJournalContent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MorningJournalDialog(
    onDismiss: () -> Unit,
    onSave: (DailyJournalContent) -> Unit
) {
    var goals by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Morning Journal", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = { Text("What are your top 3 goals for today?") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = gratitude,
                    onValueChange = { gratitude = it },
                    label = { Text("What are you grateful for?") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(DailyJournalContent(type = "morning", gratitude = gratitude, goals = goals.split("\n")))
            }) { Text("Save Journal") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TradeLogDialog(
    onDismiss: () -> Unit,
    onSave: (Trade) -> Unit
) {
    var asset by remember { mutableStateOf("") }
    var bias by remember { mutableStateOf("buy") }
    var entryPrice by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Paper Trade", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = asset,
                    onValueChange = { asset = it },
                    label = { Text("Asset (e.g. BTC/USD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = bias == "buy", onClick = { bias = "buy" }, label = { Text("Buy") })
                    FilterChip(selected = bias == "sell", onClick = { bias = "sell" }, label = { Text("Sell") })
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = entryPrice,
                    onValueChange = { entryPrice = it },
                    label = { Text("Entry Price") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Trade(
                    asset = asset,
                    bias = bias,
                    entryPrice = entryPrice.toDoubleOrNull() ?: 0.0,
                    stopLoss = 0.0,
                    takeProfit = 0.0,
                    notes = "",
                    date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                ))
            }) { Text("Record Trade") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
@Composable
fun EveningJournalDialog(
    onDismiss: () -> Unit,
    onSave: (DailyJournalContent) -> Unit
) {
    var accomplishments by remember { mutableStateOf("") }
    var challenges by remember { mutableStateOf("") }
    var lessons by remember { mutableStateOf("") }
    var mood by remember { mutableFloatStateOf(3f) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Evening Journal", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(
                    value = accomplishments,
                    onValueChange = { accomplishments = it },
                    label = { Text("What did you accomplish today?") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = challenges,
                    onValueChange = { challenges = it },
                    label = { Text("What challenges did you face?") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = lessons,
                    onValueChange = { lessons = it },
                    label = { Text("What lessons did you learn?") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Overall Mood", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = mood,
                    onValueChange = { mood = it },
                    valueRange = 1f..5f,
                    steps = 3
                )
                Text("Level: ${mood.toInt()}", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(DailyJournalContent(
                    type = "evening",
                    accomplishments = accomplishments,
                    challenges = challenges,
                    lessons = lessons,
                    mood = mood.toInt()
                ))
            }) { Text("Complete Reflection") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
