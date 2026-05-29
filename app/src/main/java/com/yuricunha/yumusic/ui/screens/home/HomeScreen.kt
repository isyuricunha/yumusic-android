package com.yuricunha.yumusic.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.home.viewmodel.HomeViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArtistClick: (String) -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState.artists is ScreenState.Loading || uiState.randomAlbums is ScreenState.Loading
    val isError = uiState.artists is ScreenState.Error && uiState.randomAlbums is ScreenState.Error

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = viewModel::loadContent,
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            isLoading && !isError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            }
            isError -> {
                val msg = (uiState.artists as? ScreenState.Error)?.message
                    ?: stringResource(R.string.error_not_configured)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = msg, style = MaterialTheme.typography.bodyLarge, color = TextTertiary)
                }
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                val artists = (uiState.artists as? ScreenState.Success)?.data as? List<ArtistDto>
                @Suppress("UNCHECKED_CAST")
                val randomAlbums = (uiState.randomAlbums as? ScreenState.Success)?.data as? List<AlbumDto>

                HomeContent(
                    artists = artists ?: emptyList(),
                    randomAlbums = randomAlbums ?: emptyList(),
                    onArtistClick = onArtistClick,
                    onAlbumClick = onAlbumClick,
                    onSettingsClick = onSettingsClick,
                    getCoverArtUrl = viewModel::getCoverArtUrl,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    artists: List<ArtistDto>,
    randomAlbums: List<AlbumDto>,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        // ── Trending / Featured Hero ─────────────────────────────────────
        if (artists.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                ) {
                    // Hero background image
                    AsyncImage(
                        model = getCoverArtUrl(artists.first().coverArt),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Gradient overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Background.copy(alpha = 0.3f),
                                        Background,
                                    ),
                                    startY = 0f,
                                    endY = 600f,
                                )
                            ),
                    )

                    // Settings button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Background.copy(alpha = 0.5f))
                            .clickable { onSettingsClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.screen_settings),
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // Hero text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                    ) {
                        Text(
                            text = "Featured Artist",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryAccent,
                            letterSpacing = 2.sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = artists.first().name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (artists.first().albumCount != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${artists.first().albumCount} albums",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TextPrimary)
                                .clickable { onArtistClick(artists.first().id) }
                                .padding(horizontal = 24.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "View Artist",
                                style = MaterialTheme.typography.labelLarge,
                                color = Background,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        // ── Quick link to Library ────────────────────────────────────────
        if (artists.size > 1) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Browse All Artists",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(artists.drop(1).take(10)) { artist ->
                        Column(
                            modifier = Modifier
                                .width(140.dp)
                                .clickable { onArtistClick(artist.id) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AlbumArt(
                                coverArtUrl = getCoverArtUrl(artist.coverArt),
                                contentDescription = artist.name,
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(140.dp),
                                cornerRadius = 70.dp,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = artist.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${artist.albumCount ?: 0} albums",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                            )
                        }
                    }
                }
            }
        }

        // ── Random Albums ────────────────────────────────────────────────
        if (randomAlbums.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = "${randomAlbums.size} albums",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(randomAlbums) { album ->
                        Column(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onAlbumClick(album.id) },
                        ) {
                            AlbumArt(
                                coverArtUrl = getCoverArtUrl(album.coverArt),
                                contentDescription = album.name,
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(160.dp)
                                    .aspectRatio(1f),
                                cornerRadius = 6.dp,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = album.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (!album.artist.isNullOrEmpty()) {
                                Text(
                                    text = album.artist,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
