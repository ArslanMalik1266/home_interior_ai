package org.yourappdev.homeinterior.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.yourappdev.homeinterior.domain.usecase.AddCreditsUseCase
import org.yourappdev.homeinterior.domain.usecase.FetchGeneratedRoomUseCase
import org.yourappdev.homeinterior.domain.usecase.GenerateRoomUseCase
import org.yourappdev.homeinterior.domain.usecase.RegisterGuestUseCase
import org.yourappdev.homeinterior.domain.usecase.SpendCreditsUseCase
import org.yourappdev.homeinterior.navigation.NavigationViewModel
import org.yourappdev.homeinterior.ui.authentication.AuthViewModel
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomsViewModel
import org.yourappdev.homeinterior.ui.OnBoarding.OnBoardingViewModel

val viewModelModule = module {
    factory { AddCreditsUseCase(get()) }
    factory { SpendCreditsUseCase(get()) }
    factory { RegisterGuestUseCase(get()) }
    factory { GenerateRoomUseCase(get()) }
    factory { FetchGeneratedRoomUseCase(get()) }

    single {
        AuthViewModel(
            verifyOtpUseCase = get(),
            loginUseCase = get(),
            logoutUseCase = get(),
            resendOtpUseCase = get(),
            registerGuestUseCase = get(),
            repository = get(),
            settings = get()
        )
    }

    single {
        RoomsViewModel(
            roomsRepository = get(),
            addCreditsUseCase = get(),
            authViewModel = get(),
            draftsRepository = get(),
            recentGeneratedRepository = get(),
            spendCreditsUseCase = get(),
            generateRoomUseCase = get(),
            fetchGeneratedRoomUseCase = get(),
            httpClient = get(),
        )
    }

    viewModelOf(::NavigationViewModel)
    viewModelOf(::OnBoardingViewModel)
}