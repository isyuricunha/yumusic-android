package com.yuricunha.yumusic.ui.screens.home.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.api.TrackDto
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

data class HomeUiState(
    val artists: ScreenState<List<ArtistDto>> = ScreenState.Loading,
    val randomAlbums: ScreenState<List<AlbumDto>> = ScreenState.Loading,
    val randomSongs: List<TrackDto> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val repository: SubsonicRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            val config = settingsRepository.serverConfig.first()
            if (!config.isConfigured) {
                _uiState.value = HomeUiState(
                    artists = ScreenState.Error(application.getString(R.string.error_not_configured)),
                    randomAlbums = ScreenState.Error(application.getString(R.string.error_not_configured)),
                )
                return@launch
            }
            _uiState.value = HomeUiState(
                artists = ScreenState.Loading,
                randomAlbums = ScreenState.Loading,
            )

            // Fetch artists and random albums concurrently
            val artistsDeferred = viewModelScope.launch {
                repository.getArtists()
                    .onSuccess { artists ->
                        _uiState.value = _uiState.value.copy(artists = ScreenState.Success(artists))
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            artists = ScreenState.Error(e.message ?: application.getString(R.string.error_not_configured))
                        )
                    }
            }

            val albumsDeferred = viewModelScope.launch {
                repository.getRandomAlbums(size = 10)
                    .onSuccess { albums ->
                        _uiState.value = _uiState.value.copy(randomAlbums = ScreenState.Success(albums))
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(randomAlbums = ScreenState.Success(emptyList()))
                    }
            }

            // Wait for both to complete
            artistsDeferred.join()
            albumsDeferred.join()
        }

        // Fetch random songs
        viewModelScope.launch {
            repository.getRandomSongs(10)
                .onSuccess { songs ->
                    _uiState.value = _uiState.value.copy(randomSongs = songs)
                }
                .onFailure { /* ignore */ }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
