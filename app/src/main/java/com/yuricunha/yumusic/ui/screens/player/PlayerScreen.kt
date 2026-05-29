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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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

    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    PlayerScreenContent(
        title = playerState.title.ifEmpty { stringResource(R.string.player_track_title) },
        artist = playerState.artist.ifEmpty { stringResource(R.string.player_artist_name) },
        coverArtUrl = playerState.coverArtUrl,
        isPlaying = playerState.isPlaying,
        progress = playerState.progress,
        onPlayPauseClick = viewModel::playPause,
        onNextClick = viewModel::skipToNext,
        onPreviousClick = viewModel::skipToPrevious,
        onSeek = viewModel::seekToProgress,
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
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
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
        )

        // MASSIVE artwork — the hero element, like Tidal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            AlbumArt(
                coverArtUrl = coverArtUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                cornerRadius = 8.dp,
            )
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
                .padding(horizontal = 48.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.player_previous),
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp),
                )
            }

            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) {
                        stringResource(R.string.player_pause)
                    } else {
                        stringResource(R.string.player_play)
                    },
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp),
                )
            }

            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.player_next),
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
