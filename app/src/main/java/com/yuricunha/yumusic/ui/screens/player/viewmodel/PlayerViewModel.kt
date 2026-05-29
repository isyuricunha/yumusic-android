package com.yuricunha.yumusic.ui.screens.player.viewmodel

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.yuricunha.yumusic.player.PlayerConnection
import com.yuricunha.yumusic.player.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerConnection: PlayerConnection,
    private val application: Application,
) : ViewModel() {

    val playerState: StateFlow<PlayerUiState> = playerConnection.uiState

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
