package org.yourappdev.homeinterior.domain.repo

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.model.LogoutDomainModel
import org.yourappdev.homeinterior.domain.model.UserDetail
import org.yourappdev.homeinterior.domain.model.VerifyResponse

interface AuthRepository {
    suspend fun login(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<VerifyResponse>
    suspend fun verifyOtp(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): Result<DeviceLinkResult>

    suspend fun logout(
        packageName: String,
        userEmail: String,
        deviceId: String
    ): Result<LogoutDomainModel>

    suspend fun getProfileAndCredits(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<DeviceLinkResult>
}