package com.bn.todo.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bn.todo.di.ApplicationModule.context
import com.bn.todo.util.DataStoreKeys.DATASTORE_NAME
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)
object DataStoreMgr {

    suspend inline fun <reified T> setPreferences(
        key: Preferences.Key<T>,
        vararg values: T
    ) {
        context.dataStore.edit { prefs ->
            values.forEach { prefs[key] = it }
        }
    }

    suspend inline fun <reified T> getPreferences(
        key: Preferences.Key<T>,
        default: T? = null
    ) =
        context.dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else throw it
        }
            .map { prefs ->
                prefs[key] ?: run {
                    default ?: {
                        when (T::class) {
                            Int::class -> 0
                            Long::class -> 0L
                            Float::class -> 0f
                            Double::class -> 0.0
                            else -> null
                        }
                    } as T
                }
            }
}

object DataStoreKeys {
    internal const val DATASTORE_NAME = "preferences"
    val CURRENT_LIST = intPreferencesKey("current_list")
    val NOT_FIRST_LAUNCH = booleanPreferencesKey("not_first_launch")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val SORT_PREF = intPreferencesKey("sort_pref")
}