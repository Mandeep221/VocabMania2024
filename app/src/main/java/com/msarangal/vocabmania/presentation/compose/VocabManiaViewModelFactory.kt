package com.msarangal.vocabmania.presentation.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.msarangal.vocabmania.presentation.compose.favorites.FavoritesViewModel
import com.msarangal.vocabmania.presentation.compose.home.HomeViewModel
import com.msarangal.vocabmania.presentation.compose.onboarding.OnboardingViewModel
import com.msarangal.vocabmania.presentation.compose.progress.ProgressViewModel
import com.msarangal.vocabmania.presentation.compose.review.ReviewViewModel
import com.msarangal.vocabmania.presentation.compose.sessioncomplete.SessionCompleteViewModel
import com.msarangal.vocabmania.shared.VocabManiaShared
import com.msarangal.vocabmania.shared.domain.reminder.DailyReminderScheduler

class VocabManiaViewModelFactory(
    private val shared: VocabManiaShared,
    private val reminderScheduler: DailyReminderScheduler,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return when {
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(shared) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(shared, reminderScheduler) as T
            modelClass.isAssignableFrom(ReviewViewModel::class.java) ->
                ReviewViewModel(shared, savedStateHandle) as T
            modelClass.isAssignableFrom(SessionCompleteViewModel::class.java) ->
                SessionCompleteViewModel(shared, savedStateHandle) as T
            modelClass.isAssignableFrom(ProgressViewModel::class.java) ->
                ProgressViewModel(shared) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) ->
                FavoritesViewModel(shared) as T
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
