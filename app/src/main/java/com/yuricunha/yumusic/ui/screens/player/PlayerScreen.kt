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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.TextSnippet  // lyrics icon
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.player.viewmodel.PlayerViewModel
import com.yuricunha.yumusic.ui.theme.Background
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

    LaunchedEffect(Unit) {
        viewModel.connect()
    }

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
        onToggleLyrics = viewModel::toggleLyrics,
        onPlayPauseClick = viewModel::playPause,
        onNextClick = viewModel::skipToNext,
        onPreviousClick = viewModel::skipToPrevious,
        onSeek = viewModel::seekToProgress,
        onToggleShuffle = viewModel::toggleShuffle,
        onCycleRepeat = viewModel::cycleRepeatMode,
        onBackClick = onBackClick,
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
    onToggleLyrics: () -> Unit = {},
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit = {},
    onCycleRepeat: () -> Unit = {},
    onBackClick: () -> Unit,
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
                        Icon(
                            imageVector = Icons.Filled.TextSnippet,
                            contentDescription = "Lyrics",
                            tint = if (showLyrics) PrimaryAccent else TextSecondary,
                        )
                    }
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}
