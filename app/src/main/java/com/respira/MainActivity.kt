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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.respira.ui.components.AppTab
import com.respira.ui.components.RespiraBottomNavBar
import com.respira.ui.components.RespiraLogoMark
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
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.SurfaceBackground
import com.respira.ui.theme.TextPrimary
import com.respira.viewmodel.WellnessViewModel
import kotlinx.coroutines.delay

enum class ScreenDestination {
    SPLASH,
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
            ScreenDestination.SPLASH -> {
                SplashScreen(onTimeout = { currentDestination = ScreenDestination.MAIN_TABS })
            }

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

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200L)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RespiraLogoMark(size = 110.dp)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Respira",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Breathwork & Lung Wellness",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondarySage,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
