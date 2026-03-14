package org.yourappdev.homeinterior.data.mapper

import org.yourappdev.homeinterior.data.remote.dto.DeviceLinkResponseDto
import org.yourappdev.homeinterior.data.remote.dto.LogoutResponseDto
import org.yourappdev.homeinterior.data.remote.dto.UserDetailDto
import org.yourappdev.homeinterior.data.remote.dto.UserDto
import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.model.LogoutDomainModel
import org.yourappdev.homeinterior.domain.model.UserDetail
import org.yourappdev.homeinterior.domain.model.UserDetailDomainModel

fun DeviceLinkResponseDto.toDomain(): DeviceLinkResult {
    return DeviceLinkResult(
        status = this.status ?: "error",
        userEmail = this.userEmail ?: "",
        freeCredits = this.freeCredits ?: 0,
        purchaseCredits = this.purchasedCredits ?: 0,
        totalCredits = this.totalCredits ?: 0,
        authProvider = this.authProvider ?: "unknown",
        // User null ho sakta hai, isliye ?. lagao aur default empty object do
        user = this.user?.toDomain() ?: UserDetail(
            id = 0, appId = "", deviceId = "", freeCredits = "0",
            totalCredits = 0, userEmail = "", createdAt = "", updatedAt = ""
        )
    )
}

fun UserDto?.toDomain(): UserDetail { // Nullable extension function
    return UserDetail(
        id = this?.id ?: 0,
        appId = this?.appId ?: "",
        deviceId = this?.deviceId ?: "",
        freeCredits = this?.freeCredits ?: "",
        totalCredits = this?.totalCredits ?: 0,
        userEmail = this?.userEmail ?: "",
        createdAt = this?.createdAt ?: "",
        updatedAt = this?.updatedAt ?: ""
    )
}

fun LogoutResponseDto.toDomain() = LogoutDomainModel(
    status = status,
    freeCredits = freeCredits,
    totalCredits = totalCredits,
    user = user?.toDomain()
)

fun UserDetailDto.toDomain() = UserDetailDomainModel(
    id = id, appId = appId, deviceId = deviceId,
    freeCredits = freeCredits, totalCredits = totalCredits,
    userEmail = userEmail, createdAt = createdAt, updatedAt = updatedAt
)