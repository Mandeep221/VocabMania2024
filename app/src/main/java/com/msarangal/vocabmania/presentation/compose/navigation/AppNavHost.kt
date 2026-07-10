package com.msarangal.vocabmania.presentation.compose.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msarangal.vocabmania.presentation.compose.VocabManiaViewModelFactory
import com.msarangal.vocabmania.presentation.compose.home.HomeScreen
import com.msarangal.vocabmania.presentation.compose.home.HomeViewModel
import com.msarangal.vocabmania.presentation.compose.onboarding.OnboardingScreen
import com.msarangal.vocabmania.presentation.compose.onboarding.OnboardingViewModel
import com.msarangal.vocabmania.presentation.compose.review.ReviewScreen
import com.msarangal.vocabmania.presentation.compose.review.ReviewViewModel
import com.msarangal.vocabmania.presentation.compose.sessioncomplete.SessionCompleteScreen
import com.msarangal.vocabmania.presentation.compose.sessioncomplete.SessionCompleteViewModel
import com.msarangal.vocabmania.shared.SharedBootstrap
import com.msarangal.vocabmania.shared.VocabManiaShared

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val shared = remember { SharedBootstrap.requireShared() }
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = resolveStartDestination(shared)
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val viewModelFactory = remember(shared) { VocabManiaViewModelFactory(shared) }

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
    ) {
        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(factory = viewModelFactory)
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = viewModel,
                onStartReview = {
                    navController.navigate(Routes.REVIEW)
                },
            )
        }
        composable(Routes.REVIEW) {
            val viewModel: ReviewViewModel = viewModel(factory = viewModelFactory)
            ReviewScreen(
                viewModel = viewModel,
                onSessionComplete = { reviewedCount, lastScheduleFeedback ->
                    navController.navigate(Routes.sessionComplete(reviewedCount, lastScheduleFeedback)) {
                        popUpTo(Routes.REVIEW) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.SESSION_COMPLETE,
            arguments = listOf(
                navArgument(Routes.REVIEWED_COUNT_ARG) { type = NavType.IntType },
                navArgument(Routes.LAST_SCHEDULE_FEEDBACK_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            val viewModel: SessionCompleteViewModel = viewModel(factory = viewModelFactory)
            SessionCompleteScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
            )
        }
    }
}

private suspend fun resolveStartDestination(shared: VocabManiaShared): String {
    val settings = shared.getUserSettingsUseCase()
    return if (settings.onboardingComplete) Routes.HOME else Routes.ONBOARDING
}
