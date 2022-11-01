package com.bn.todo.data.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface UserPrefRepository {
    suspend fun setSortPref(sortPref: Int): Preferences
    fun getSortPref(default: Int): Flow<Int>

    suspend fun setShowCompleted(showCompleted: Boolean): Preferences
    fun getShowCompleted(default: Boolean): Flow<Boolean>

    suspend fun setIsFirstTimeLaunch(isFirstTimeLaunch: Boolean): Preferences
    fun getIsFirstTimeLaunch(default: Boolean = true): Flow<Boolean>
    fun getCurrentListId(default: Long): Flow<Long>

    suspend fun setCurrentListId(id: Long): Preferences
}