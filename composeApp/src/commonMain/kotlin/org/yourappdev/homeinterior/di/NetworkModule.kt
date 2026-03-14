package org.yourappdev.homeinterior.di

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.remote.createHttpClientManual

val networkModule = module {

    single<HttpClient> { createHttpClientManual(get()) }
}