package org.yourappdev.homeinterior.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.remote.service.AuthService
import org.yourappdev.homeinterior.data.remote.service.RoomService
import org.yourappdev.homeinterior.data.repository.AuthRepositoryImpl
import org.yourappdev.homeinterior.data.repository.CreditsRepositoryImpl
import org.yourappdev.homeinterior.data.repository.RoomRepositoryImpl
import org.yourappdev.homeinterior.domain.repo.AuthRepository
import org.yourappdev.homeinterior.domain.repo.CreditsRepository
import org.yourappdev.homeinterior.domain.repo.RoomsRepository
import org.yourappdev.homeinterior.utils.NetworkConfig

val dataModule = module {
    single { NetworkConfig.API_KEY }

    single {
        AuthService(
            client = get(),
            baseUrl ="https://dashboard.urdufonts.com/api",
            apiKey = get()
        )
    }
    single {
        RoomService(
            client = get(),
            baseUrl = "https://interior.shabbirhussain.com/api"
        )
    }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<RoomsRepository> { RoomRepositoryImpl(get()) }
    single<CreditsRepository> { CreditsRepositoryImpl(get()) }
}