package com.bn.todo.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPrefRepository {
    suspend fun setSortPref(sortPref: Int)
    fun getSortPref(default: Int): Flow<Int>

    suspend fun setShowCompleted(showCompleted: Boolean)
    fun getShowCompleted(default: Boolean): Flow<Boolean>

    suspend fun setIsFirstTimeLaunch(isFirstTimeLaunch: Boolean)
    fun getIsFirstTimeLaunch(default: Boolean = true): Flow<Boolean>
    fun getCurrentListId(default: Long): Flow<Long>

    suspend fun setCurrentListId(id: Long)
}