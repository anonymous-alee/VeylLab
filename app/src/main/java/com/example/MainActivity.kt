package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.AleetrixViewModel
import com.example.ui.screens.*
import com.example.ui.theme.AleetrixTheme
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Landing : Screen("landing", "Home", Icons.Default.Home)
    object Dashboard : Screen("dashboard", "Portal", Icons.Default.Dashboard)
    object Clients : Screen("clients", "CRM", Icons.Default.People)
    object Packages : Screen("packages", "Services", Icons.Default.CardGiftcard)
    object Payments : Screen("payments", "Payments", Icons.Default.ReceiptLong)
    object AiAutomation : Screen("ai_copilot", "AI Copilot", Icons.Default.AutoAwesome)
    object Blog : Screen("blog", "Blog", Icons.Default.Article)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Auth : Screen("auth", "Auth", Icons.Default.Lock)
}

class MainActivity : ComponentActivity() {

    private val viewModel: AleetrixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            AleetrixTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainNavigationItems = listOf(
                    Screen.Landing,
                    Screen.Dashboard,
                    Screen.Clients,
                    Screen.Payments,
                    Screen.AiAutomation,
                    Screen.Settings
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != Screen.Auth.route) {
                            NavigationBar(
                                containerColor = DarkSurface,
                                contentColor = TextPrimary,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .border(width = 0.5.dp, color = DarkBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            ) {
                                mainNavigationItems.forEach { screen ->
                                    val isSelected = currentRoute == screen.route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                tint = if (isSelected) NeonYellow else TextSecondary
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) NeonYellow else TextSecondary
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = NeonYellow.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("nav_item_${screen.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Landing.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Landing.route) {
                            LandingScreen(
                                viewModel = viewModel,
                                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                                onNavigateToPackages = { navController.navigate(Screen.Packages.route) },
                                onNavigateToBlog = { navController.navigate(Screen.Blog.route) }
                            )
                        }

                        composable(Screen.Dashboard.route) {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToClients = { navController.navigate(Screen.Clients.route) },
                                onNavigateToPayments = { navController.navigate(Screen.Payments.route) },
                                onNavigateToAi = { navController.navigate(Screen.AiAutomation.route) },
                                onNavigateToPackages = { navController.navigate(Screen.Packages.route) }
                            )
                        }

                        composable(Screen.Clients.route) {
                            ClientsScreen(viewModel = viewModel)
                        }

                        composable(Screen.Packages.route) {
                            PackagesScreen(viewModel = viewModel)
                        }

                        composable(Screen.Payments.route) {
                            PaymentsScreen(viewModel = viewModel)
                        }

                        composable(Screen.AiAutomation.route) {
                            AiAutomationScreen(viewModel = viewModel)
                        }

                        composable(Screen.Blog.route) {
                            BlogScreen(viewModel = viewModel)
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel,
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme }
                            )
                        }

                        composable(Screen.Auth.route) {
                            AuthScreen(
                                viewModel = viewModel,
                                onLoginSuccess = { navController.navigate(Screen.Dashboard.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}
