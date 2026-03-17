package com.dayforge.app.ui.screens.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Star
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayforge.app.DayForgeApplication
import com.dayforge.app.ui.components.*
import com.dayforge.app.ui.viewmodels.*
import com.dayforge.app.data.models.ForgeCategory

@Composable
fun SummaryScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as DayForgeApplication).repository
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()
    val streak by viewModel.streak.collectAsState()
    
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
            StatCard("Blocks Done", "${stats.completedBlocks}/${stats.totalBlocks}", ForgeCategory.Hacking.icon, Modifier.weight(1f))
            StatCard("Trading", "${stats.tradesLogged} Trades", ForgeCategory.Trading.icon, Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Study Time", "${stats.studyHours}h", ForgeCategory.Study.icon, Modifier.weight(1f))
            StatCard("Daily Streak", "$streak Days", Icons.Default.TrendingUp, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        Text("AI Forge-Sight", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        RecommendationCard(recommendation)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
