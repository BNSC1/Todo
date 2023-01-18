package com.bn.todo.data.repository

import com.bn.todo.constant.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefRepositoryImpl @Inject constructor(
    private val dataStoreMgr: DataStoreMgr
) : UserPrefRepository {

    override suspend fun setSortPref(sortPref: Int) =
        dataStoreMgr.setPreference(DataStoreKeys.SORT_PREF, sortPref)

    override fun getSortPref(default: Int) =
        dataStoreMgr.getPreference(DataStoreKeys.SORT_PREF, default)

    override suspend fun setShowCompleted(showCompleted: Boolean) =
        dataStoreMgr.setPreference(DataStoreKeys.SHOW_COMPLETED, showCompleted)

    override fun getShowCompleted(default: Boolean) =
        dataStoreMgr.getPreference(DataStoreKeys.SHOW_COMPLETED, default)

    override suspend fun setIsFirstTimeLaunch(isFirstTimeLaunch: Boolean) =
        dataStoreMgr.setPreference(DataStoreKeys.IS_FIRST_LAUNCH, isFirstTimeLaunch)

    override fun getIsFirstTimeLaunch(default: Boolean) =
        dataStoreMgr.getPreference(DataStoreKeys.IS_FIRST_LAUNCH, default)


    override fun getCurrentListId(default: Long) =
        dataStoreMgr.getPreference(DataStoreKeys.CURRENT_LIST, default)

    override suspend fun setCurrentListId(id: Long) =
        dataStoreMgr.setPreference(DataStoreKeys.CURRENT_LIST, id)

}