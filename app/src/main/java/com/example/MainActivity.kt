package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.statusking.ai.data.local.PreferencesManager
import com.statusking.ai.data.local.StatusDatabase
import com.statusking.ai.repository.DefaultAiRepository
import com.statusking.ai.repository.DefaultAuthRepository
import com.statusking.ai.repository.StatusRepository
import com.statusking.ai.service.AdsManager
import com.statusking.ai.service.BillingManager
import com.statusking.ai.service.ExportManager
import com.statusking.ai.ui.MainViewModel
import com.statusking.ai.ui.MainViewModelFactory
import com.statusking.ai.ui.components.NavDestination
import com.statusking.ai.ui.components.StatusKingBottomNav
import com.statusking.ai.ui.screens.CreateScreen
import com.statusking.ai.ui.screens.HomeScreen
import com.statusking.ai.ui.screens.PremiumScreen
import com.statusking.ai.ui.screens.ProfileScreen
import com.statusking.ai.ui.screens.SplashScreen
import com.statusking.ai.ui.screens.TrendingScreen
import com.statusking.ai.ui.theme.StatusKingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = StatusDatabase.getDatabase(applicationContext)
        val preferencesManager = PreferencesManager(applicationContext)
        val statusRepository = StatusRepository(database.statusDao())
        val aiRepository = DefaultAiRepository()
        val authRepository = DefaultAuthRepository(preferencesManager)
        val billingManager = BillingManager(preferencesManager)
        val adsManager = AdsManager(preferencesManager)
        val exportManager = ExportManager(applicationContext)

        val viewModelFactory = MainViewModelFactory(
            statusRepository = statusRepository,
            aiRepository = aiRepository,
            authRepository = authRepository,
            billingManager = billingManager,
            adsManager = adsManager,
            exportManager = exportManager,
            preferencesManager = preferencesManager
        )

        setContent {
            val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
            val themeMode by viewModel.themeMode.collectAsState()
            val isPremium by viewModel.isPremium.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val favorites by viewModel.favorites.collectAsState()
            val creations by viewModel.creations.collectAsState()
            val allStatuses by viewModel.allStatuses.collectAsState()
            val editingStatus by viewModel.editingStatus.collectAsState()

            var showSplash by remember { mutableStateOf(true) }
            var currentNav by remember { mutableStateOf(NavDestination.HOME) }

            StatusKingTheme(themeMode = themeMode) {
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (currentNav != NavDestination.PREMIUM) {
                                StatusKingBottomNav(
                                    currentDestination = currentNav,
                                    onNavigate = { destination ->
                                        currentNav = destination
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(
                                targetState = currentNav,
                                animationSpec = tween(durationMillis = 250),
                                label = "screen_navigation_crossfade"
                            ) { screen ->
                                when (screen) {
                                    NavDestination.HOME -> {
                                        HomeScreen(
                                            isPremium = isPremium,
                                            aiRepository = viewModel.aiRepository,
                                            onNavigateToPremium = { currentNav = NavDestination.PREMIUM },
                                            onNavigateToCreateWithStatus = { status ->
                                                viewModel.setEditingStatus(status)
                                                currentNav = NavDestination.CREATE
                                            },
                                            onFavoriteToggle = { status ->
                                                viewModel.toggleFavorite(status)
                                            }
                                        )
                                    }
                                    NavDestination.CREATE -> {
                                        CreateScreen(
                                            initialStatus = editingStatus,
                                            isPremium = isPremium,
                                            aiRepository = viewModel.aiRepository,
                                            statusRepository = viewModel.statusRepository,
                                            exportManager = viewModel.exportManager,
                                            onNavigateToPremium = { currentNav = NavDestination.PREMIUM }
                                        )
                                    }
                                    NavDestination.TRENDING -> {
                                        TrendingScreen(
                                            statuses = allStatuses,
                                            isPremium = isPremium,
                                            onNavigateToPremium = { currentNav = NavDestination.PREMIUM },
                                            onFavoriteToggle = { status ->
                                                viewModel.toggleFavorite(status)
                                            },
                                            onUseInEditor = { status ->
                                                viewModel.setEditingStatus(status)
                                                currentNav = NavDestination.CREATE
                                            }
                                        )
                                    }
                                    NavDestination.PREMIUM -> {
                                        PremiumScreen(
                                            billingManager = viewModel.billingManager,
                                            isPremium = isPremium,
                                            onClose = { currentNav = NavDestination.HOME }
                                        )
                                    }
                                    NavDestination.PROFILE -> {
                                        ProfileScreen(
                                            userProfile = currentUser,
                                            isPremium = isPremium,
                                            themeMode = themeMode,
                                            favorites = favorites,
                                            creations = creations,
                                            statusRepository = viewModel.statusRepository,
                                            authRepository = viewModel.authRepository,
                                            preferencesManager = viewModel.preferencesManager,
                                            onNavigateToPremium = { currentNav = NavDestination.PREMIUM },
                                            onUseInEditor = { status ->
                                                viewModel.setEditingStatus(status)
                                                currentNav = NavDestination.CREATE
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
