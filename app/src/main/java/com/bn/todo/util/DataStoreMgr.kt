package com.bn.todo.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.bn.todo.constant.DataStoreKeys.DATASTORE_NAME
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
    ) {
        context.dataStore.edit { prefs ->
            prefs[key] = values
        }
    }


    inline fun <reified T> getPreference(
        key: Preferences.Key<T>,
        default: T
    ) = context.dataStore.data
        .catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else throw it
    }
        .map { prefs ->
            prefs[key] ?: default
        }
}