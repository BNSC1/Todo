package com.bn.todo.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bn.todo.di.ApplicationModule.context
import com.bn.todo.util.DataStoreKeys.DATASTORE_NAME
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)
object DataStoreMgr {

    suspend inline fun <reified T> savePreferences(
        key: Preferences.Key<T>,
        vararg values: MutablePreferences
    ) =
        context.dataStore.edit { prefs ->
            values.forEach { prefs[key] = it as T }
        }

    inline fun <reified T> readPreferences(
        key: Preferences.Key<T>
    ) = context.dataStore.data.map { prefs ->
        prefs[key] as T
    }
}

object DataStoreKeys {
    internal const val DATASTORE_NAME = "preferences"
    val CURRENT_LIST = stringPreferencesKey("current_list")
}