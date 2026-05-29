package com.yuricunha.yumusic.ui.screens.search

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.search.viewmodel.SearchResults
import com.yuricunha.yumusic.ui.screens.search.viewmodel.SearchViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.BackgroundElevated
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.HoverRipple
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onArtistClick: (String) -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreenContent(
        uiState = uiState,
        onSearch = viewModel::search,
        onArtistClick = onArtistClick,
        onAlbumClick = onAlbumClick,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    uiState: ScreenState<*>,
    onSearch: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.screen_search),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background,
            ),
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_placeholder),
                    color = TextTertiary,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = HoverRipple,
                focusedContainerColor = BackgroundElevated,
                unfocusedContainerColor = BackgroundElevated,
                cursorColor = PrimaryAccent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                )
            }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val results = uiState.data as? SearchResults
                if (results != null && results.artists.isEmpty() && results.albums.isEmpty() && results.songs.isEmpty()) {
                    if (query.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.search_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                        )
                    }
                } else if (results != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        // Artists section
                        if (results.artists.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.search_section_artists).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                                )
                            }
                            items(results.artists) { artist ->
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
                                        modifier = Modifier.size(48.dp),
                                        cornerRadius = 24.dp,
                                    )
                                    Text(
                                        text = artist.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }

                        // Albums section
                        if (results.albums.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(if (results.artists.isNotEmpty()) 8.dp else 16.dp))
                                Text(
                                    text = stringResource(R.string.search_section_albums).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                                )
                            }
                            items(results.albums) { album ->
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
                                        modifier = Modifier.size(48.dp),
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
                                        album.artist?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TextTertiary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Songs section
                        if (results.songs.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(if (results.artists.isNotEmpty() || results.albums.isNotEmpty()) 8.dp else 16.dp))
                                Text(
                                    text = stringResource(R.string.search_section_songs).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                                )
                            }
                            items(results.songs) { song ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    AlbumArt(
                                        coverArtUrl = getCoverArtUrl(song.coverArt),
                                        contentDescription = song.title,
                                        modifier = Modifier.size(48.dp),
                                        cornerRadius = 4.dp,
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = song.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        song.artist?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = TextTertiary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                }
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
