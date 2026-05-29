package com.yuricunha.yumusic.ui.screens.settings.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.SubsonicApiService
import com.yuricunha.yumusic.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

data class SettingsUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val settingsRepository: SettingsRepository,
    private val apiService: SubsonicApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val config = settingsRepository.serverConfig.first()
            _uiState.value = SettingsUiState(
                serverUrl = config.serverUrl,
                username = config.username,
                password = config.password,
            )
        }
    }

    fun onServerUrlChange(url: String) {
        _uiState.value = _uiState.value.copy(serverUrl = url, errorMessage = null, saveSuccess = false)
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, errorMessage = null, saveSuccess = false)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null, saveSuccess = false)
    }

    fun save() {
        val state = _uiState.value
        if (state.serverUrl.isBlank()) {
            _uiState.value = state.copy(errorMessage = application.getString(R.string.error_server_url_required))
            return
        }
        if (state.username.isBlank()) {
            _uiState.value = state.copy(errorMessage = application.getString(R.string.error_username_required))
            return
        }
        if (state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = application.getString(R.string.error_password_required))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            try {
                // Step 1: Save config to DataStore
                settingsRepository.saveConfig(
                    serverUrl = state.serverUrl,
                    username = state.username,
                    password = state.password,
                )

                // Step 2: Wait for DataStore to flush and interceptor to pick up new config
                delay(300)

                // Step 3: Actually test the connection with a real API call
                Log.d(TAG, "Testing connection to ${state.serverUrl}")
                val response = apiService.getArtists(
                    username = state.username,
                    password = state.password,
                )

                // Step 4: Check for API-level errors
                val body = response.response
                val error = body?.error
                if (error != null) {
                    Log.w(TAG, "API error: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = error.message,
                    )
                    return@launch
                }
                if (body?.status != "ok") {
                    Log.w(TAG, "API status: ${body?.status}")
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = application.getString(R.string.error_connection_failed),
                    )
                    return@launch
                }

                // Step 5: Success — server is reachable and credentials are valid
                Log.d(TAG, "Connection successful")
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)

            } catch (e: Exception) {
                Log.e(TAG, "Connection failed", e)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.localizedMessage ?: application.getString(R.string.error_connection_failed),
                )
            }
        }
    }
}
