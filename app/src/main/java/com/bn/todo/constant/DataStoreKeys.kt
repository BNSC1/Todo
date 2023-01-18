package com.bn.todo.constant

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object DataStoreKeys {
    internal const val DATASTORE_NAME = "preferences"
    val CURRENT_LIST = longPreferencesKey("current_list")
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val SORT_PREF = intPreferencesKey("sort_pref")
}