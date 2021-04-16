package com.daniel.geofenceapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.daniel.geofenceapp.util.Constants.PREFERENCE_FIRST_LAUNCH
import com.daniel.geofenceapp.util.Constants.PREFERENCE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)


class DataStoreRepository(private val context: Context) {

    private object PreferenceKey {
        val firstLaunch = booleanPreferencesKey(PREFERENCE_FIRST_LAUNCH)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveFirstLaunch(firstLaunch: Boolean){
        dataStore.edit { preference ->
            preference[PreferenceKey.firstLaunch] = firstLaunch
        }
    }

    val readFirstLaunch: Flow<Boolean> = dataStore.data
            .catch { exception ->
                if(exception is IOException){
                    emit(emptyPreferences())
                }else{
                    throw exception
                }
            }
            .map { preference ->
                val firstLaunch  = preference[PreferenceKey.firstLaunch] ?: true
                firstLaunch
            }


}