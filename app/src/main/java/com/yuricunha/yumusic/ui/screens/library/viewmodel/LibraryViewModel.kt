package com.yuricunha.yumusic.ui.screens.library.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.api.BookmarkDto
import com.yuricunha.yumusic.data.api.GenreDto
import com.yuricunha.yumusic.data.api.NowPlayingEntry
import com.yuricunha.yumusic.data.api.PlaylistDto
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

data class LibraryUiState(
    val artists: ScreenState<List<ArtistDto>> = ScreenState.Loading,
    val playlists: ScreenState<List<PlaylistDto>> = ScreenState.Loading,
    val starred: ScreenState<List<TrackDto>> = ScreenState.Loading,
    val genres: ScreenState<List<GenreDto>> = ScreenState.Loading,
    val bookmarks: ScreenState<List<BookmarkDto>> = ScreenState.Loading,
    val nowPlaying: ScreenState<List<NowPlayingEntry>> = ScreenState.Loading,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val application: Application,
    private val repository: SubsonicRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            val config = settingsRepository.serverConfig.first()
            if (!config.isConfigured) {
                _uiState.value = LibraryUiState(
                    artists = ScreenState.Error(application.getString(R.string.error_not_configured)),
                    playlists = ScreenState.Error(application.getString(R.string.error_not_configured)),
                )
                return@launch
            }
            _uiState.value = LibraryUiState(
                artists = ScreenState.Loading,
                playlists = ScreenState.Loading,
                starred = ScreenState.Loading,
                genres = ScreenState.Loading,
                bookmarks = ScreenState.Loading,
                nowPlaying = ScreenState.Loading,
            )

            // Fetch artists, playlists, starred, genres, bookmarks, nowPlaying concurrently
            launch {
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

            launch {
                repository.getPlaylists()
                    .onSuccess { playlists ->
                        _uiState.value = _uiState.value.copy(playlists = ScreenState.Success(playlists))
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(playlists = ScreenState.Success(emptyList()))
                    }
            }

            launch {
                repository.getStarred()
                    .onSuccess { starred ->
                        _uiState.value = _uiState.value.copy(
                            starred = ScreenState.Success(starred.songs ?: emptyList())
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(starred = ScreenState.Success(emptyList()))
                    }
            }

            launch {
                repository.getGenres()
                    .onSuccess { genres ->
                        _uiState.value = _uiState.value.copy(
                            genres = ScreenState.Success(genres)
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(genres = ScreenState.Success(emptyList()))
                    }
            }

            launch {
                repository.getBookmarks()
                    .onSuccess { bookmarks ->
                        _uiState.value = _uiState.value.copy(bookmarks = ScreenState.Success(bookmarks))
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(bookmarks = ScreenState.Success(emptyList()))
                    }
            }

            launch {
                repository.getNowPlaying()
                    .onSuccess { entries ->
                        _uiState.value = _uiState.value.copy(nowPlaying = ScreenState.Success(entries))
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(nowPlaying = ScreenState.Success(emptyList()))
                    }
            }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
