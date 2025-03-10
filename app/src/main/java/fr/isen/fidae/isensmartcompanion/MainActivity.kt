package fr.isen.fidae.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import fr.isen.fidae.isensmartcompanion.database.AppDatabase
import fr.isen.fidae.isensmartcompanion.screens.EventsScreen
import fr.isen.fidae.isensmartcompanion.screens.HistoryScreen
import fr.isen.fidae.isensmartcompanion.screens.MainScreen
import fr.isen.fidae.isensmartcompanion.screens.TabView
import fr.isen.fidae.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeTab = TabBarItem(title = getString(R.string.navbar_home), selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
            val eventsTab = TabBarItem(title = getString(R.string.navbar_events), selectedIcon = Icons.Filled.DateRange, unselectedIcon = Icons.Outlined.DateRange, badgeAmount = 7)
            val historyTab = TabBarItem(title = getString(R.string.navbar_history), selectedIcon = Icons.Filled.List, unselectedIcon = Icons.Outlined.List)

            val tabBarItems = listOf(homeTab, eventsTab, historyTab)

            val db = Room.databaseBuilder(
                LocalContext.current,
                AppDatabase::class.java, "messagesDB"
            ).fallbackToDestructiveMigration().build()


            val navController = rememberNavController()

            ISENSmartCompanionTheme {
                Scaffold( bottomBar = {
                    TabView(tabBarItems, navController)
                },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = homeTab.title) {
                            composable(homeTab.title) {
                                MainScreen(innerPadding, db)
                            }
                            composable(eventsTab.title) {
                                EventsScreen(innerPadding)
                            }
                            composable(historyTab.title) {
                                HistoryScreen(innerPadding, db)
                            }
                        }

                    }

                }
            }
        }
    }
}