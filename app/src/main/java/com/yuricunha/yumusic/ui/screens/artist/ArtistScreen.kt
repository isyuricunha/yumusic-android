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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.AlbumDto
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

    ArtistScreenContent(
        artistName = uiState.artistName,
        albumsState = uiState.albums,
        onAlbumClick = onAlbumClick,
        onBackClick = onBackClick,
        onRetry = viewModel::loadAlbums,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreenContent(
    artistName: String,
    albumsState: ScreenState<*>,
    onAlbumClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
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
                containerColor = Background,
            ),
        )

        when (albumsState) {
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
                        text = albumsState.message,
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
                val albums = albumsState.data as? List<AlbumDto> ?: emptyList()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Hero artwork — first album's art, or a large placeholder
                    if (albums.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                AlbumArt(
                                    coverArtUrl = getCoverArtUrl(albums.firstOrNull()?.coverArt),
                                    contentDescription = artistName,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    cornerRadius = 0.dp,
                                )
                                // Gradient overlay for text readability
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                colors = listOf(
                                                    androidx.compose.ui.graphics.Color.Transparent,
                                                    Background,
                                                )
                                            )
                                        ),
                                )
                                Text(
                                    text = artistName,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Normal,
                                    ),
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                                )
                            }
                        }
                    } else if (artistName.isNotEmpty()) {
                        item {
                            Text(
                                text = artistName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                ),
                                color = TextPrimary,
                                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                            )
                        }
                    }

                    // Albums section header
                    if (albums.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.screen_albums_count, albums.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextTertiary,
                                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
                            )
                        }
                    }

                    // Album list — clean rows with artwork
                    itemsIndexed(albums) { index, album ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAlbumClick(album.id) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            AlbumArt(
                                coverArtUrl = getCoverArtUrl(album.coverArt),
                                contentDescription = album.name,
                                modifier = Modifier.size(56.dp),
                                cornerRadius = 4.dp,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = album.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                val info = buildString {
                                    album.year?.let { append(it.toString()) }
                                    album.songCount?.let {
                                        if (isNotEmpty()) append(" · ")
                                        append(stringResource(R.string.album_track_count, it))
                                    }
                                }
                                if (info.isNotEmpty()) {
                                    Text(
                                        text = info,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextTertiary,
                                    )
                                }
                            }
                        }
                        if (index < albums.lastIndex) {
                            HorizontalDivider(
                                color = Divider,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(start = 86.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
