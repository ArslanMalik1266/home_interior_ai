package org.yourappdev.homeinterior.domain.repo

import org.yourappdev.homeinterior.data.local.entities.UserInfoEntity

interface UserRepository{
    suspend fun getUserProfileData(): UserInfoEntity
}