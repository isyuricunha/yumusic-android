package com.yuricunha.yumusic.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class ServerConfig(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
) {
    val isConfigured: Boolean get() = serverUrl.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
    }

    val serverConfig: Flow<ServerConfig> = context.dataStore.data.map { preferences ->
        ServerConfig(
            serverUrl = preferences[Keys.SERVER_URL] ?: "",
            username = preferences[Keys.USERNAME] ?: "",
            password = preferences[Keys.PASSWORD] ?: "",
        )
    }

    suspend fun saveConfig(serverUrl: String, username: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SERVER_URL] = serverUrl
            preferences[Keys.USERNAME] = username
            preferences[Keys.PASSWORD] = password
        }
    }
}
