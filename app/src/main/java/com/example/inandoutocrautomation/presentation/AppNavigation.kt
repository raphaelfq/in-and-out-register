package com.example.inandoutocrautomation.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
