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
        dataStoreMgr.setPreferences(DataStoreKeys.SORT_PREF, sortPref)

    suspend fun getSortPref(default: Int) =
        dataStoreMgr.getPreferences(DataStoreKeys.SORT_PREF, default)

    suspend fun setShowCompleted(showCompleted: Boolean) =
        dataStoreMgr.setPreferences(DataStoreKeys.SHOW_COMPLETED, showCompleted)

    suspend fun getShowCompleted(default: Boolean) =
        dataStoreMgr.getPreferences(DataStoreKeys.SHOW_COMPLETED, default)

    suspend fun setNotFirstTimeLaunch(isNotFirstTimeLaunch: Boolean) =
        dataStoreMgr.setPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, isNotFirstTimeLaunch)

    suspend fun getNotFirstTimeLaunch(default: Boolean = false) =
        dataStoreMgr.getPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, default)


    suspend fun getCurrentListId(default: Int) =
        dataStoreMgr.getPreferences(DataStoreKeys.CURRENT_LIST, default)

    suspend fun setCurrentListId(id: Int) =
        dataStoreMgr.setPreferences(DataStoreKeys.CURRENT_LIST, id)

//    companion object {
//        @Volatile
//        private var INSTANCE: UserPrefRepository? = null
//
//        fun getInstance(context: Context): UserPrefRepository {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE?.let {
//                    return it
//                }
//
//                val instance = UserPrefRepository(context)
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}