package org.yourappdev.homeinterior.data.mapper

import org.yourappdev.homeinterior.data.remote.dto.GuestSessionDto
import org.yourappdev.homeinterior.data.remote.dto.GuestUserDto
import org.yourappdev.homeinterior.domain.model.GuestSession
import org.yourappdev.homeinterior.domain.model.GuestUser

fun GuestSessionDto.toDomain(): GuestSession {
    return GuestSession(
        status = status,
        freeCredits = freeCredits,
        totalCredits = totalCredits,
        isNew = isNew,
        user = user.toDomain()
    )
}

fun GuestUserDto.toDomain(): GuestUser {
    return GuestUser(
        id = id,
        appId = appId,
        deviceId = deviceId,
        freeCredits = freeCredits,
        totalCredits = totalCredits,
        userEmail = userEmail
    )
}