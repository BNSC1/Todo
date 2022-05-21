package com.bn.todo.data.repository

import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefRepository @Inject constructor(
    private val dataStoreMgr: DataStoreMgr
) {

    suspend fun setSortPref(sortPref: Int) =
        dataStoreMgr.setPreference(DataStoreKeys.SORT_PREF, sortPref)

    fun getSortPref(default: Int) =
        dataStoreMgr.getPreference(DataStoreKeys.SORT_PREF, default)

    suspend fun setShowCompleted(showCompleted: Boolean) =
        dataStoreMgr.setPreference(DataStoreKeys.SHOW_COMPLETED, showCompleted)

    fun getShowCompleted(default: Boolean) =
        dataStoreMgr.getPreference(DataStoreKeys.SHOW_COMPLETED, default)

    suspend fun setNotFirstTimeLaunch(isNotFirstTimeLaunch: Boolean) =
        dataStoreMgr.setPreference(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)

    fun getNotFirstTimeLaunch(default: Boolean = false) =
        dataStoreMgr.getPreference(DataStoreKeys.NOT_FIRST_LAUNCH, default)


    fun getCurrentListId(default: Int) =
        dataStoreMgr.getPreference(DataStoreKeys.CURRENT_LIST, default)

    suspend fun setCurrentListId(id: Int) =
        dataStoreMgr.setPreference(DataStoreKeys.CURRENT_LIST, id)

}