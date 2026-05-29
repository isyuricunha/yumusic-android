package com.yuricunha.yumusic.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PlayerConnection"

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val coverArtUrl: String? = null,
    val progress: Float = 0f,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isAvailable: Boolean = false,
)

@Singleton
class PlayerConnection @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var isConnecting = false

    @OptIn(UnstableApi::class)
    fun connect() {
        if (mediaController != null || isConnecting) return
        isConnecting = true

        try {
            val sessionToken = SessionToken(
                context,
                ComponentName(context, PlayerService::class.java),
            )
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture?.addListener({
                try {
                    val controller = controllerFuture?.get()
                    if (controller != null) {
                        mediaController = controller
                        controller.addListener(playerListener)
                        updateState()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to connect to player service", e)
                } finally {
                    isConnecting = false
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to build media controller", e)
            isConnecting = false
        }
    }

    fun disconnect() {
        try {
            mediaController?.removeListener(playerListener)
            mediaController?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting player", e)
        }
        mediaController = null
        try {
            controllerFuture?.cancel(true)
        } catch (_: Exception) {
            // Ignore cancellation errors
        }
        controllerFuture = null
        isConnecting = false
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }
    }

    private fun updateState() {
        val player = mediaController ?: return
        try {
            val mediaMetadata = player.currentMediaItem?.mediaMetadata
            _uiState.value = PlayerUiState(
                isPlaying = player.isPlaying,
                title = mediaMetadata?.title?.toString() ?: "",
                artist = player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "",
                coverArtUrl = player.currentMediaItem?.mediaMetadata?.artworkUri?.toString(),
                progress = if (player.duration > 0) {
                    player.currentPosition.toFloat() / player.duration.toFloat()
                } else 0f,
                duration = player.duration,
                currentPosition = player.currentPosition,
                isAvailable = true,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error updating player state", e)
        }
    }

    fun play(mediaItems: List<MediaItem>, startIndex: Int = 0) {
        val player = mediaController ?: return
        try {
            player.setMediaItems(mediaItems, startIndex, 0L)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing media", e)
        }
    }

    fun playPause() {
        val player = mediaController ?: return
        try {
            if (player.isPlaying) player.pause() else player.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling play/pause", e)
        }
    }

    fun seekTo(position: Long) {
        try {
            mediaController?.seekTo(position)
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking", e)
        }
    }

    fun seekToProgress(progress: Float) {
        val player = mediaController ?: return
        try {
            val duration = player.duration
            if (duration > 0) {
                player.seekTo((progress * duration).toLong())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking to progress", e)
        }
    }

    fun skipToNext() {
        try {
            mediaController?.seekToNext()
        } catch (e: Exception) {
            Log.e(TAG, "Error skipping to next", e)
        }
    }

    fun skipToPrevious() {
        try {
            mediaController?.seekToPrevious()
        } catch (e: Exception) {
            Log.e(TAG, "Error skipping to previous", e)
        }
    }

    fun buildMediaItem(
        streamUrl: String,
        id: String,
        title: String,
        artist: String?,
        album: String?,
        coverArtUrl: String?,
    ): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(coverArtUrl?.let { android.net.Uri.parse(it) })
                    .build()
            )
            .build()
    }
}
