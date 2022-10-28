package com.bn.todo.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bn.todo.util.DataStoreKeys.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

@Singleton
class DataStoreMgr @Inject constructor(@ApplicationContext val context: Context) {

    suspend inline fun <reified T> clearPreference(key: Preferences.Key<T>) =
        context.dataStore.edit { prefs -> prefs.remove(key) }

    suspend inline fun <reified T> setPreference(
        key: Preferences.Key<T>,
        values: T
    ) = context.dataStore.edit { prefs ->
        prefs[key] = values
    }


    inline fun <reified T> getPreference(
        key: Preferences.Key<T>,
        default: T? = null,
    ) = context.dataStore.data.catch {
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
                        Boolean::class -> false
                        String::class -> ""
                        else -> null
                    }
                } as T
            }
        }
}

object DataStoreKeys {
    internal const val DATASTORE_NAME = "preferences"
    val CURRENT_LIST = longPreferencesKey("current_list")
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val SORT_PREF = intPreferencesKey("sort_pref")
}