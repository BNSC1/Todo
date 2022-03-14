package com.bn.todo.ui.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr
import javax.inject.Inject

class EntryViewModel @Inject constructor() : BaseViewModel() {
    suspend fun getIsNotFirstLaunch() =
        DataStoreMgr.getPreferences(DataStoreKeys.NOT_FIRST_LAUNCH, false)
}