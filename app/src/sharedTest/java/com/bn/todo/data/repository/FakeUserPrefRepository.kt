package com.bn.todo.data.repository

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeUserPrefRepository @Inject constructor(): UserPrefRepository {
    private var isFirstTimeLaunch = true
    private var sortPref = 0
    private var showCompleted = true
    private var currentListId = 0L

    override suspend fun setSortPref(sortPref: Int) {
        this.sortPref = sortPref
    }

    override fun getSortPref(default: Int) = flow {
        emit(sortPref)
    }

    override suspend fun setShowCompleted(showCompleted: Boolean) {
        this.showCompleted = showCompleted
    }

    override fun getShowCompleted(default: Boolean) = flow {
        emit(showCompleted)
    }

    override suspend fun setIsFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        this.isFirstTimeLaunch = isFirstTimeLaunch
    }

    override fun getIsFirstTimeLaunch(default: Boolean) = flow {
        emit(isFirstTimeLaunch)
    }

    override fun getCurrentListId(default: Long) = flow {
        emit(currentListId)
    }

    override suspend fun setCurrentListId(id: Long) {
        this.currentListId = id
    }
}