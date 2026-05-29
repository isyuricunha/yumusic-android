package com.yuricunha.yumusic.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
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
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val artistsState by viewModel.artistsState.collectAsState()

    HomeScreenContent(
        artistsState = artistsState,
        onArtistClick = onArtistClick,
        onSettingsClick = onSettingsClick,
        onRetry = viewModel::loadArtists,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    artistsState: ScreenState<*>,
    onArtistClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.screen_home),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.screen_settings),
                        tint = TextSecondary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
        )

        when (artistsState) {
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
                        text = artistsState.message,
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
                val artists = artistsState.data as? List<ArtistDto>
                if (artists.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.home_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        // Featured artist — first one, large artwork
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = stringResource(R.string.home_section_artists),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .clickable { onArtistClick(artists[0].id) },
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    AlbumArt(
                                        coverArtUrl = getCoverArtUrl(artists[0].coverArt),
                                        contentDescription = artists[0].name,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(200.dp),
                                        cornerRadius = 4.dp,
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artists[0].name,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Normal,
                                            ),
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        if (artists[0].albumCount != null) {
                                            Text(
                                                text = stringResource(R.string.library_albums_count, artists[0].albumCount ?: 0),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TextTertiary,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Artist grid — 2 columns
                        if (artists.size > 1) {
                            item {
                                Spacer(modifier = Modifier.height(28.dp))
                            }
                            items(artists.drop(1).chunked(2)) { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    row.forEach { artist ->
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onArtistClick(artist.id) },
                                        ) {
                                            AlbumArt(
                                                coverArtUrl = getCoverArtUrl(artist.coverArt),
                                                contentDescription = artist.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                cornerRadius = 4.dp,
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = artist.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            if (artist.albumCount != null) {
                                                Text(
                                                    text = stringResource(R.string.library_albums_count, artist.albumCount),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = TextTertiary,
                                                )
                                            }
                                        }
                                    }
                                    // Fill empty space if odd number
                                    if (row.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
