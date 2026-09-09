package com.respira

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.respira.ui.components.AppTab
import com.respira.ui.components.RespiraBottomNavBar
import com.respira.ui.screens.AboutScreen
import com.respira.ui.screens.ExercisesScreen
import com.respira.ui.screens.GuidedSessionScreen
import com.respira.ui.screens.HistoryScreen
import com.respira.ui.screens.HomeScreen
import com.respira.ui.screens.LungCapacityTestScreen
import com.respira.ui.screens.ProfileScreen
import com.respira.ui.screens.SessionSummaryDialog
import com.respira.ui.screens.SettingsScreen
import com.respira.ui.theme.MyApplicationTheme
import com.respira.ui.theme.SurfaceBackground
import com.respira.viewmodel.WellnessViewModel

enum class ScreenDestination {
    MAIN_TABS,
    ABOUT,
    SETTINGS,
    LUNG_TEST
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val wellnessViewModel: WellnessViewModel = viewModel()
                RespiraApp(viewModel = wellnessViewModel)
            }
        }
    }
}

@Composable
fun RespiraApp(viewModel: WellnessViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var currentDestination by remember { mutableStateOf(ScreenDestination.MAIN_TABS) }

    val activeExercise by viewModel.activeExercise.collectAsState()
    val completedSummary by viewModel.completedSummary.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    // Guided Session takes priority if an active exercise is underway
    if (activeExercise != null) {
        BackHandler {
            viewModel.exitSessionEarly()
        }
        GuidedSessionScreen(
            exercise = activeExercise!!,
            viewModel = viewModel
        )
    } else {
        when (currentDestination) {
            ScreenDestination.ABOUT -> {
                BackHandler { currentDestination = ScreenDestination.MAIN_TABS }
                AboutScreen(onNavigateBack = { currentDestination = ScreenDestination.MAIN_TABS })
            }

            ScreenDestination.SETTINGS -> {
                BackHandler { currentDestination = ScreenDestination.MAIN_TABS }
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = ScreenDestination.MAIN_TABS }
                )
            }

            ScreenDestination.LUNG_TEST -> {
                BackHandler { currentDestination = ScreenDestination.MAIN_TABS }
                LungCapacityTestScreen(
                    viewModel = viewModel,
                    onDismiss = { currentDestination = ScreenDestination.MAIN_TABS }
                )
            }

            ScreenDestination.MAIN_TABS -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SurfaceBackground,
                    bottomBar = {
                        RespiraBottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(SurfaceBackground)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { tab ->
                            when (tab) {
                                AppTab.EXERCISES -> {
                                    ExercisesScreen(
                                        viewModel = viewModel,
                                        onNavigateToSettings = { currentDestination = ScreenDestination.SETTINGS },
                                        onSelectExercise = { exercise ->
                                            viewModel.startExercise(exercise)
                                        }
                                    )
                                }

                                AppTab.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToAbout = { currentDestination = ScreenDestination.ABOUT },
                                        onNavigateToExercises = { currentTab = AppTab.EXERCISES },
                                        onStartRecommendedExercise = { exercise ->
                                            viewModel.startExercise(exercise)
                                        },
                                        onStartLungTest = {
                                            viewModel.startLungCapacityTest()
                                            currentDestination = ScreenDestination.LUNG_TEST
                                        }
                                    )
                                }

                                AppTab.PROFILE -> {
                                    ProfileScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Session Completion Summary Dialog
    if (completedSummary != null) {
        SessionSummaryDialog(
            summary = completedSummary!!,
            onDismiss = {
                viewModel.dismissSummary()
                currentTab = AppTab.HOME
            }
        )
    }
}
