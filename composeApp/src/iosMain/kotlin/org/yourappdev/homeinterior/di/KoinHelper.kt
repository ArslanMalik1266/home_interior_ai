package org.yourappdev.homeinterior.di

import org.koin.core.context.startKoin
import org.yourappdev.homeinterior.di.appModule
import org.yourappdev.homeinterior.di.platformModule

class KoinHelper {
    fun doInitKoin() {
        startKoin {
            modules(appModule() + platformModule())
        }
    }
}
