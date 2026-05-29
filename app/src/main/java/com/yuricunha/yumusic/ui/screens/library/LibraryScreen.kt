package com.yuricunha.yumusic.ui.screens.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.library.viewmodel.LibraryViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LibraryScreenContent(
        uiState = uiState,
        onArtistClick = onArtistClick,
        onRetry = viewModel::loadArtists,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenContent(
    uiState: ScreenState<*>,
    onArtistClick: (String) -> Unit,
    onRetry: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.screen_library),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
        )

        when (uiState) {
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
                        text = uiState.message,
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
                val artists = uiState.data as? List<ArtistDto>
                if (artists.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.library_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(artists) { index, artist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArtistClick(artist.id) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                AlbumArt(
                                    coverArtUrl = getCoverArtUrl(artist.coverArt),
                                    contentDescription = artist.name,
                                    modifier = Modifier.size(52.dp),
                                    cornerRadius = 4.dp,
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = artist.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if (artist.albumCount != null && artist.albumCount > 0) {
                                        Text(
                                            text = stringResource(R.string.library_albums_count, artist.albumCount),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextTertiary,
                                        )
                                    }
                                }
                            }
                            if (index < artists.lastIndex) {
                                HorizontalDivider(
                                    color = Divider,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(start = 82.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
