package org.yourappdev.homeinterior.navigation

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.yourappdev.homeinterior.utils.Constants

class NavigationViewModel(
    private val settings: Settings
) : ViewModel() {

    private val _state = MutableStateFlow(NavigationState())
    val state: StateFlow<NavigationState> = _state.asStateFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {

        val isOnboardingDone = settings.getBoolean(
            key = Constants.ONBOARDING,
            defaultValue = false
        )

        val isLoggedIn = settings.getBoolean(
            key = Constants.LOGIN,
            defaultValue = false
        )

        val startDestination = when {
            !isOnboardingDone -> Routes.OnBoarding.toString()
            else -> Routes.BaseAppScreen.toString()
        }

        _state.value = NavigationState(startDestination)
    }
}