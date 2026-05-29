package com.yuricunha.yumusic.ui.screens.player.viewmodel

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.player.PlayerConnection
import com.yuricunha.yumusic.player.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerConnection: PlayerConnection,
    private val repository: SubsonicRepository,
    private val application: Application,
) : ViewModel() {

    val playerState: StateFlow<PlayerUiState> = playerConnection.uiState

    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    private val _showLyrics = MutableStateFlow(false)
    val showLyrics: StateFlow<Boolean> = _showLyrics.asStateFlow()

    private var lastTrackTitle: String? = null

    init {
        observeTrackChanges()
    }

    private fun observeTrackChanges() {
        viewModelScope.launch {
            playerConnection.uiState.collect { state ->
                if (state.title.isNotEmpty() && state.title != lastTrackTitle) {
                    lastTrackTitle = state.title
                    fetchLyrics(state.artist, state.title)
                }
            }
        }
    }

    private suspend fun fetchLyrics(artist: String, title: String) {
        if (artist.isEmpty() || title.isEmpty()) {
            _lyrics.value = null
            return
        }
        repository.getLyrics(artist, title)
            .onSuccess { data -> _lyrics.value = data.value }
            .onFailure { _lyrics.value = null }
    }

    fun toggleLyrics() {
        _showLyrics.value = !_showLyrics.value
    }

    fun connect() {
        playerConnection.connect()
    }

    fun disconnect() {
        playerConnection.disconnect()
    }

    @OptIn(UnstableApi::class)
    fun play(mediaItems: List<MediaItem>, startIndex: Int = 0) {
        playerConnection.play(mediaItems, startIndex)
    }

    fun playPause() {
        playerConnection.playPause()
    }

    fun seekToProgress(progress: Float) {
        playerConnection.seekToProgress(progress)
    }

    fun toggleShuffle() {
        playerConnection.toggleShuffle()
    }

    fun cycleRepeatMode() {
        playerConnection.cycleRepeatMode()
    }

    fun skipToNext() {
        playerConnection.skipToNext()
    }

    fun skipToPrevious() {
        playerConnection.skipToPrevious()
    }
}
