package com.yuricunha.yumusic.ui.screens.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.playlist.viewmodel.PlaylistViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    onBackClick: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PlaylistScreenContent(
        playlistName = uiState.playlistName,
        tracksState = uiState.tracks,
        onTrackClick = { index ->
            viewModel.playTrack(index)
            onNavigateToPlayer()
        },
        onBackClick = onBackClick,
        onRetry = viewModel::loadTracks,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreenContent(
    playlistName: String,
    tracksState: ScreenState<*>,
    onTrackClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = playlistName.ifEmpty { stringResource(R.string.screen_playlist) },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
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

        when (tracksState) {
            is ScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            }
            is ScreenState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = tracksState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextTertiary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.action_retry),
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryAccent,
                        modifier = Modifier.clickable { onRetry() },
                    )
                }
            }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val tracks = tracksState.data as? List<TrackDto> ?: emptyList()

                if (tracks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.playlist_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // Track count header
                        item {
                            Text(
                                text = stringResource(R.string.album_track_count, tracks.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextTertiary,
                                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                            )
                        }

                        itemsIndexed(tracks) { index, track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTrackClick(index) }
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary,
                                    modifier = Modifier.width(20.dp),
                                    textAlign = TextAlign.End,
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if (!track.artist.isNullOrEmpty()) {
                                        Text(
                                            text = track.artist,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextTertiary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                                val durationMin = (track.duration ?: 0) / 60
                                val durationSec = (track.duration ?: 0) % 60
                                Text(
                                    text = stringResource(R.string.album_track_duration, durationMin, durationSec),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary,
                                )
                            }
                            if (index < tracks.lastIndex) {
                                HorizontalDivider(
                                    color = Divider,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(start = 58.dp),
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}