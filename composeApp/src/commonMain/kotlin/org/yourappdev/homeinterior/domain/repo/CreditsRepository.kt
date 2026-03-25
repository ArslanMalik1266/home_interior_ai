package org.yourappdev.homeinterior.domain.repo

import org.yourappdev.homeinterior.domain.model.CreditResponse
import org.yourappdev.homeinterior.domain.model.SpendCreditsResponse


interface CreditsRepository {
    suspend fun addCredits(email: String, amount: Int): Result<CreditResponse>
        suspend fun spendCredits(
            userEmail: String,
            amount: Int,
            deviceId: String,
            packageName: String
        ): Result<SpendCreditsResponse>
    suspend fun spendCreditsGuest(
        amount: Int,
        deviceId: String,
        packageName: String
    ): Result<SpendCreditsResponse>
    }
