package com.yuricunha.yumusic.ui.screens.artist.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.api.ArtistInfo
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.player.PlayerConnection
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistUiState(
    val artistName: String = "",
    val albums: ScreenState<List<AlbumDto>> = ScreenState.Loading,
    val biography: String? = null,
    val topSongs: List<TrackDto> = emptyList(),
    val similarArtists: List<ArtistDto> = emptyList(),
)

@HiltViewModel
class ArtistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubsonicRepository,
    private val playerConnection: PlayerConnection,
) : ViewModel() {

    private val artistId: String = savedStateHandle["artistId"] ?: ""

    private val _uiState = MutableStateFlow(ArtistUiState())
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
        loadBiography()
        loadTopSongs()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(albums = ScreenState.Loading)
            repository.getAlbumsByArtist(artistId)
                .onSuccess { (name, albums) ->
                    _uiState.value = _uiState.value.copy(
                        artistName = name,
                        albums = ScreenState.Success(albums),
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        albums = ScreenState.Error(e.message ?: "Unknown error"),
                    )
                }
        }
    }

    private fun loadBiography() {
        viewModelScope.launch {
            repository.getArtistInfo(artistId)
                .onSuccess { info ->
                    _uiState.value = _uiState.value.copy(
                        biography = info.biography,
                        similarArtists = info.similarArtists ?: emptyList(),
                    )
                }
                .onFailure { /* BIO unavailable */ }
        }
    }

    private fun loadTopSongs() {
        viewModelScope.launch {
            val name = _uiState.value.artistName
            if (name.isEmpty()) return@launch
            repository.getTopSongs(name, 5)
                .onSuccess { songs ->
                    _uiState.value = _uiState.value.copy(topSongs = songs)
                }
                .onFailure { /* Top songs unavailable */ }
        }
    }

    fun playAll() {
        val state = _uiState.value.albums
        if (state !is ScreenState.Success) return
        val albums = state.data
        if (albums.isEmpty()) return
        // Play all tracks from first album
        val firstAlbumId = albums.first().id
        viewModelScope.launch {
            repository.getTracksByAlbum(firstAlbumId)
                .onSuccess { data ->
                    val mediaItems = data.tracks.map { track ->
                        val streamUrl = repository.getStreamUrl(track.id)
                        val coverArtUrl = track.coverArt?.let { repository.getCoverArtUrl(it) }
                        playerConnection.buildMediaItem(
                            streamUrl = streamUrl,
                            id = track.id,
                            title = track.title,
                            artist = track.artist,
                            album = track.album,
                            coverArtUrl = coverArtUrl,
                        )
                    }
                    if (mediaItems.isNotEmpty()) {
                        playerConnection.play(mediaItems, 0)
                    }
                }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
