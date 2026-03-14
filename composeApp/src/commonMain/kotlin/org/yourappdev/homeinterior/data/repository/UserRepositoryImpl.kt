package org.yourappdev.homeinterior.data.repository

import org.yourappdev.homeinterior.data.local.dao.ProfileDao
import org.yourappdev.homeinterior.data.local.entities.UserInfoEntity
import org.yourappdev.homeinterior.domain.repo.UserRepository

class UserRepositoryImpl(val userProfileDao: ProfileDao) : UserRepository {
    override suspend fun getUserProfileData(): UserInfoEntity {
        return userProfileDao.getUserInfo()
    }

}