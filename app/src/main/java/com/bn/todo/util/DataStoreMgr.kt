package com.bn.todo.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
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
        prefs[key] ?: run {
            when (T::class) {
                Int::class -> 0
                Long::class -> 0L
                Float::class -> 0f
                Double::class -> 0.0
                Boolean::class -> false
                else -> null
            }
        } as T
    }
}

object DataStoreKeys {
    internal const val DATASTORE_NAME = "preferences"
    val CURRENT_LIST = stringPreferencesKey("current_list")
    val NOT_FIRST_LAUNCH = booleanPreferencesKey("not_first_launch")
}