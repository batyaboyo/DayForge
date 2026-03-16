package com.dayforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.dayforge.app.ui.screens.daily.DailyScreen
import com.dayforge.app.ui.screens.goals.GoalsScreen
import com.dayforge.app.ui.screens.review.ReviewScreen
import com.dayforge.app.ui.screens.summary.SummaryScreen
import com.dayforge.app.ui.theme.DayForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DayForgeTheme {
                MainScreen()
            }
        }
    }
}

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    object Daily : NavItem("daily", Icons.Default.DateRange, "Daily")
    object Goals : NavItem("goals", Icons.Default.Star, "Goals")
    object Summary : NavItem("summary", Icons.Default.List, "Summary")
    object Review : NavItem("review", Icons.Default.Edit, "Review")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navItems = listOf(
        NavItem.Daily,
        NavItem.Goals,
        NavItem.Summary,
        NavItem.Review
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, navItems)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Daily.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 2 } },
            exitTransition = { fadeOut(tween(400)) + slideOutHorizontally(tween(400)) { -it / 2 } },
            popEnterTransition = { fadeIn(tween(400)) + slideInHorizontally(tween(400)) { -it / 2 } },
            popExitTransition = { fadeOut(tween(400)) + slideOutHorizontally(tween(400)) { it / 2 } }
        ) {
            composable(NavItem.Daily.route) { DailyScreen() }
            composable(NavItem.Goals.route) { GoalsScreen() }
            composable(NavItem.Summary.route) { SummaryScreen() }
            composable(NavItem.Review.route) { ReviewScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<NavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
