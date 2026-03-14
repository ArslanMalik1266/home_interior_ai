package org.yourappdev.homeinterior.domain.repo

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GuestSession

interface GuestRepository {
    suspend fun registerGuest(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession>
}