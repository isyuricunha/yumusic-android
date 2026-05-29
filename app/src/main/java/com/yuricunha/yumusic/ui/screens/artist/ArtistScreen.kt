package com.yuricunha.yumusic.ui.screens.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.artist.viewmodel.ArtistViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    onAlbumClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
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
                containerColor = Background.copy(alpha = 0f),
            ),
        )

        when (uiState.albums) {
            is ScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            }
            is ScreenState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (uiState.albums as ScreenState.Error).message,
                        color = TextTertiary,
                    )
                }
            }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val albums = (uiState.albums as ScreenState.Success).data as? List<AlbumDto> ?: emptyList()

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Hero header
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                        ) {
                            // Hero art
                            AsyncImage(
                                model = viewModel.getCoverArtUrl(albums.firstOrNull()?.coverArt),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )

                            // Gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Background.copy(alpha = 0.1f),
                                                Background,
                                            ),
                                            startY = 0f,
                                            endY = 500f,
                                        )
                                    ),
                            )

                            // Artist name overlay
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp, vertical = 24.dp),
                            ) {
                                Text(
                                    text = uiState.artistName.ifEmpty { stringResource(R.string.screen_artist) },
                                    style = MaterialTheme.typography.displaySmall,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${albums.size} albums",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                // Play button
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(TextPrimary)
                                            .clickable { viewModel.playAll() },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Play all",
                                            tint = Background,
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                    Text(
                                        text = "Play",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextPrimary,
                                    )
                                }
                            }
                        }
                    }

                    // Biography section
                    val bio = uiState.biography
                    val topSongs = uiState.topSongs
                    if (!bio.isNullOrBlank()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Biography",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 5,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    // Top Songs section
                    if (topSongs.isNotEmpty()) {
                        item {
                            Column {
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Top Songs",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        topSongs.take(5).forEach { track ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.playAll() }
                                        .padding(horizontal = 20.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AlbumArt(
                                        coverArtUrl = viewModel.getCoverArtUrl(track.coverArt),
                                        contentDescription = track.title,
                                        modifier = Modifier.size(40.dp),
                                        cornerRadius = 4.dp,
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    val dur = track.duration ?: 0
                                    Text(
                                        text = "${dur / 60}:${(dur % 60).toString().padStart(2, '0')}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary,
                                    )
                                }
                            }
                        }
                    }

                    // Albums section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Discography",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        )
                    }

                    // Album grid — 2 columns
                    val chunked = albums.chunked(2)
                    for (chunk in chunked) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                chunk.forEach { album ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onAlbumClick(album.id) },
                                    ) {
                                        AlbumArt(
                                            coverArtUrl = viewModel.getCoverArtUrl(album.coverArt),
                                            contentDescription = album.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            cornerRadius = 6.dp,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = album.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        val info = buildString {
                                            album.year?.let { append(it.toString()) }
                                            album.songCount?.let {
                                                if (isNotEmpty()) append(" · ")
                                                append("$it tracks")
                                            }
                                        }
                                        if (info.isNotEmpty()) {
                                            Text(
                                                text = info,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextTertiary,
                                            )
                                        }
                                    }
                                }
                                if (chunk.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}