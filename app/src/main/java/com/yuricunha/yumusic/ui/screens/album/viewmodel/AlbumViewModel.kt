package com.yuricunha.yumusic.ui.screens.album.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class AlbumUiState(
    val albumName: String = "",
    val artistName: String = "",
    val artistId: String? = null,
    val tracks: ScreenState<List<TrackDto>> = ScreenState.Loading,
)

@HiltViewModel
class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubsonicRepository,
    private val playerConnection: PlayerConnection,
) : ViewModel() {

    private val albumId: String = savedStateHandle["albumId"] ?: ""

    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tracks = ScreenState.Loading)
            repository.getTracksByAlbum(albumId)
                .onSuccess { data ->
                    _uiState.value = _uiState.value.copy(
                        albumName = data.albumName,
                        artistName = data.artistName,
                        artistId = data.artistId,
                        tracks = ScreenState.Success(data.tracks),
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        tracks = ScreenState.Error(e.message ?: "Unknown error"),
                    )
                }
        }
    }

    fun playTrack(index: Int) {
        val state = _uiState.value.tracks
        if (state !is ScreenState.Success) return
        val tracks = state.data
        if (index < 0 || index >= tracks.size) return

        viewModelScope.launch {
            val mediaItems = tracks.map { track ->
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
            playerConnection.play(mediaItems, index)
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }

    fun toggleStar(trackId: String, currentlyStarred: Boolean) {
        viewModelScope.launch {
            if (currentlyStarred) {
                repository.unstar(trackId)
            } else {
                repository.star(trackId)
            }
        }
    }
}
