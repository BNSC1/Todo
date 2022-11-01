package com.bn.todo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockUserPrefRepository: UserPrefRepository {
    private var isFirstTimeLaunch = true

    override suspend fun setSortPref(sortPref: Int) {
        TODO("Not yet implemented")
    }

    override fun getSortPref(default: Int): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun setShowCompleted(showCompleted: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getShowCompleted(default: Boolean): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun setIsFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        this.isFirstTimeLaunch = isFirstTimeLaunch
    }

    override fun getIsFirstTimeLaunch(default: Boolean) = flow {
        emit(isFirstTimeLaunch)
    }

    override fun getCurrentListId(default: Long): Flow<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentListId(id: Long) {
        TODO("Not yet implemented")
    }
}