package org.yourappdev.homeinterior.ui.OnBoarding

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import org.yourappdev.homeinterior.utils.Constants

class OnBoardingViewModel(val settings: Settings) : ViewModel(){
    fun onBoardingDone() {
        settings.putBoolean(Constants.ONBOARDING, true)
    }
}