package com.yuricunha.yumusic.ui.screens.playlist.viewmodel

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

data class PlaylistUiState(
    val playlistName: String = "",
    val tracks: ScreenState<List<TrackDto>> = ScreenState.Loading,
)

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubsonicRepository,
    private val playerConnection: PlayerConnection,
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"] ?: ""
    private val playlistName: String = savedStateHandle["playlistName"] ?: ""

    private val _uiState = MutableStateFlow(PlaylistUiState(playlistName = playlistName))
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(tracks = ScreenState.Loading)
            repository.getPlaylistTracks(playlistId)
                .onSuccess { tracks ->
                    _uiState.value = _uiState.value.copy(tracks = ScreenState.Success(tracks))
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
}