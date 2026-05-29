package com.yuricunha.yumusic.ui.screens.home.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.repository.SettingsRepository
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val repository: SubsonicRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _artistsState = MutableStateFlow<ScreenState<List<ArtistDto>>>(ScreenState.Loading)
    val artistsState: StateFlow<ScreenState<List<ArtistDto>>> = _artistsState.asStateFlow()

    init {
        loadArtists()
    }

    fun loadArtists() {
        viewModelScope.launch {
            val config = settingsRepository.serverConfig.first()
            if (!config.isConfigured) {
                _artistsState.value = ScreenState.Error(application.getString(R.string.error_not_configured))
                return@launch
            }
            _artistsState.value = ScreenState.Loading
            repository.getArtists()
                .onSuccess { artists ->
                    _artistsState.value = ScreenState.Success(artists)
                }
                .onFailure { e ->
                    _artistsState.value = ScreenState.Error(e.message ?: application.getString(R.string.error_not_configured))
                }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
