package com.yuricunha.yumusic.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.Player
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.player.viewmodel.PlayerViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.BackgroundElevated
import com.yuricunha.yumusic.ui.theme.HoverRipple
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val playerState by viewModel.playerState.collectAsState()
    val lyrics by viewModel.lyrics.collectAsState()
    val showLyrics by viewModel.showLyrics.collectAsState()
    val similarSongs by viewModel.similarSongs.collectAsState()
    val sleepTimer by viewModel.sleepTimerRemaining.collectAsState()

    var showSleepDialog by remember { mutableStateOf(false) }

    PlayerScreenContent(
        title = playerState.title.ifEmpty { stringResource(R.string.player_track_title) },
        artist = playerState.artist.ifEmpty { stringResource(R.string.player_artist_name) },
        coverArtUrl = playerState.coverArtUrl,
        isPlaying = playerState.isPlaying,
        progress = playerState.progress,
        shuffleModeEnabled = playerState.shuffleModeEnabled,
        repeatMode = playerState.repeatMode,
        lyrics = lyrics,
        showLyrics = showLyrics,
        similarSongs = similarSongs,
        onToggleLyrics = viewModel::toggleLyrics,
        onPlayPauseClick = viewModel::playPause,
        onNextClick = viewModel::skipToNext,
        onPreviousClick = viewModel::skipToPrevious,
        onSeek = viewModel::seekToProgress,
        onToggleShuffle = viewModel::toggleShuffle,
        onCycleRepeat = viewModel::cycleRepeatMode,
        onBackClick = onBackClick,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        sleepTimerRemaining = sleepTimer,
        onSetSleepTimer = viewModel::setSleepTimer,
        onCancelSleepTimer = viewModel::cancelSleepTimer,
        showSleepDialog = showSleepDialog,
        onToggleSleepDialog = { showSleepDialog = !showSleepDialog },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreenContent(
    title: String,
    artist: String,
    coverArtUrl: String?,
    isPlaying: Boolean,
    progress: Float,
    shuffleModeEnabled: Boolean = false,
    repeatMode: Int = Player.REPEAT_MODE_OFF,
    lyrics: String? = null,
    showLyrics: Boolean = false,
    similarSongs: List<TrackDto> = emptyList(),
    onToggleLyrics: () -> Unit = {},
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit = {},
    onCycleRepeat: () -> Unit = {},
    onBackClick: () -> Unit,
    getCoverArtUrl: (String?) -> String? = { null },
    sleepTimerRemaining: Int? = null,
    onSetSleepTimer: (Int) -> Unit = {},
    onCancelSleepTimer: () -> Unit = {},
    showSleepDialog: Boolean = false,
    onToggleSleepDialog: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Minimal top bar — just back button
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.nav_back),
                        tint = TextPrimary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
            actions = {
                if (!lyrics.isNullOrEmpty()) {
                    IconButton(onClick = onToggleLyrics) {
                        Icon(Icons.Filled.TextSnippet, "Lyrics", tint = if (showLyrics) PrimaryAccent else TextSecondary)
                    }
                }
                // Sleep timer button
                IconButton(onClick = onToggleSleepDialog) {
                    Icon(
                        Icons.Filled.Timer,
                        "Sleep timer",
                        tint = if (sleepTimerRemaining != null) PrimaryAccent else TextSecondary,
                    )
                }
            },
        )

        // MASSIVE artwork — the hero element, like Tidal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (showLyrics && !lyrics.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = lyrics,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                AlbumArt(
                    coverArtUrl = coverArtUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    cornerRadius = 8.dp,
                )
            }
        }

        // Track info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Progress slider — thin, elegant
        Slider(
            value = progress,
            onValueChange = onSeek,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = SliderDefaults.colors(
                thumbColor = PrimaryAccent,
                activeTrackColor = PrimaryAccent,
                inactiveTrackColor = HoverRipple,
            ),
        )

        // Playback controls — centered, generous spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Shuffle toggle
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = if (shuffleModeEnabled) Icons.Filled.ShuffleOn else Icons.Filled.Shuffle,
                    contentDescription = stringResource(R.string.player_shuffle),
                    tint = if (shuffleModeEnabled) PrimaryAccent else TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.player_previous),
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }

            // Play/Pause — big, centered
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(TextPrimary),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) {
                        stringResource(R.string.player_pause)
                    } else {
                        stringResource(R.string.player_play)
                    },
                    tint = Background,
                    modifier = Modifier.size(28.dp),
                )
            }

            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.player_next),
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }

            // Repeat mode toggle
            IconButton(
                onClick = onCycleRepeat,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
                        else -> Icons.Filled.Repeat
                    },
                    contentDescription = stringResource(R.string.player_repeat),
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) PrimaryAccent else TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        if (similarSongs.isNotEmpty()) {
            Text(
                text = "Similar Songs",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                similarSongs.take(10).forEach { song ->
                    Column(modifier = Modifier.width(120.dp)) {
                        AlbumArt(
                            coverArtUrl = song.coverArt?.let { getCoverArtUrl(it) },
                            contentDescription = song.title,
                            modifier = Modifier.size(120.dp),
                            cornerRadius = 4.dp,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(song.title, style = MaterialTheme.typography.labelSmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        song.artist?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = TextTertiary, maxLines = 1) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Sleep timer dialog
    if (showSleepDialog) {
        AlertDialog(
            onDismissRequest = onToggleSleepDialog,
            title = { Text(sleepTimerRemaining?.let { "Sleeping in ${it / 60}m" } ?: "Sleep Timer") },
            text = {
                Column {
                    if (sleepTimerRemaining != null) {
                        Button(onClick = onCancelSleepTimer, colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)) {
                            Text("Cancel Timer", color = Background)
                        }
                    } else {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            Button(
                                onClick = { onSetSleepTimer(mins); onToggleSleepDialog() },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TextPrimary.copy(alpha = 0.1f)),
                            ) {
                                Text("$mins minutes", color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = BackgroundElevated,
        )
    }
}
