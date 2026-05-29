package com.yuricunha.yumusic.ui.screens.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.yuricunha.yumusic.data.api.BookmarkDto
import com.yuricunha.yumusic.data.api.GenreDto
import com.yuricunha.yumusic.data.api.NowPlayingEntry
import com.yuricunha.yumusic.data.api.PlaylistDto
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.screens.library.viewmodel.LibraryUiState
import com.yuricunha.yumusic.ui.screens.library.viewmodel.LibraryViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.SurfaceCard
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String, String) -> Unit = { _, _ -> },
    onGenreClick: (String) -> Unit = {},
    onFolderClick: (String, String) -> Unit = { _, _ -> },
    onRadioClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LibraryScreenContent(
        uiState = uiState,
        onArtistClick = onArtistClick,
        onPlaylistClick = onPlaylistClick,
        onGenreClick = onGenreClick,
        onFolderClick = onFolderClick,
        onRadioClick = onRadioClick,
        onRetry = viewModel::loadContent,
        getCoverArtUrl = viewModel::getCoverArtUrl,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenContent(
    uiState: LibraryUiState,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String, String) -> Unit,
    onGenreClick: (String) -> Unit = {},
    onFolderClick: (String, String) -> Unit = { _, _ -> },
    onRadioClick: () -> Unit = {},
    onRetry: () -> Unit,
    getCoverArtUrl: (String?) -> String?,
    modifier: Modifier = Modifier,
) {
    val isLoading = uiState.artists is ScreenState.Loading || uiState.playlists is ScreenState.Loading
    val isError = uiState.artists is ScreenState.Error && uiState.playlists is ScreenState.Error

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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = msg, style = MaterialTheme.typography.bodyLarge, color = TextTertiary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.action_retry),
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryAccent,
                        modifier = Modifier.clickable { onRetry() },
                    )
                }
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                val playlists = (uiState.playlists as? ScreenState.Success)?.data as? List<PlaylistDto>
                @Suppress("UNCHECKED_CAST")
                val artists = (uiState.artists as? ScreenState.Success)?.data as? List<ArtistDto>
                @Suppress("UNCHECKED_CAST")
                val favorites = (uiState.starred as? ScreenState.Success)?.data as? List<TrackDto>
                @Suppress("UNCHECKED_CAST")
                val genres = (uiState.genres as? ScreenState.Success)?.data as? List<GenreDto>
                @Suppress("UNCHECKED_CAST")
                val bookmarks = (uiState.bookmarks as? ScreenState.Success)?.data as? List<BookmarkDto>
                @Suppress("UNCHECKED_CAST")
                val nowPlaying = (uiState.nowPlaying as? ScreenState.Success)?.data as? List<NowPlayingEntry>

                val hasPlaylists = !playlists.isNullOrEmpty()
                val hasArtists = !artists.isNullOrEmpty()
                val hasFavorites = !favorites.isNullOrEmpty()
                val hasGenres = !genres.isNullOrEmpty()

                if (!hasPlaylists && !hasArtists && !hasFavorites) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.library_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // Playlists section
                        if (hasPlaylists) {
                            item {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.library_section_playlists),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        playlists.forEach { playlist ->
                                            Column(
                                                modifier = Modifier
                                                    .width(150.dp)
                                                    .clickable { onPlaylistClick(playlist.id, playlist.name) },
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(150.dp)
                                                        .height(150.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(SurfaceCard),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    AlbumArt(
                                                        coverArtUrl = getCoverArtUrl(playlist.coverArt),
                                                        contentDescription = playlist.name,
                                                        modifier = Modifier
                                                            .width(150.dp)
                                                            .height(150.dp),
                                                        cornerRadius = 4.dp,
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = playlist.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                if (playlist.songCount != null && playlist.songCount > 0) {
                                                Text(
                                                    text = stringResource(R.string.album_track_count, playlist.songCount),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = TextTertiary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Favorites section
                        if (hasFavorites) {
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
                                        text = "Favorites",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                    )
                                    Text(
                                        text = "${favorites.size} tracks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary,
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    favorites.take(10).forEach { track ->
                                        Column(modifier = Modifier.width(160.dp)) {
                                            AlbumArt(
                                                coverArtUrl = getCoverArtUrl(track.coverArt),
                                                contentDescription = track.title,
                                                modifier = Modifier
                                                    .width(160.dp)
                                                    .height(160.dp),
                                                cornerRadius = 6.dp,
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = track.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            track.artist?.let {
                                                Text(
                                                    text = it,
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

                        // Genres section
                        if (hasGenres) {
                            item {
                                Column {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Genres",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        genres.take(20).forEach { genre ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(PrimaryAccent.copy(alpha = 0.15f))
                                                    .clickable { onGenreClick(genre.name) }
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                            ) {
                                                Text(
                                                    text = genre.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = PrimaryAccent,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Browse section
                        item(key = "browse") {
                            Box {
                                Column {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text("Browse", color = TextPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 20.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceCard).clickable { onFolderClick("0", "Music") }.padding(horizontal = 20.dp, vertical = 16.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Filled.Folder, null, tint = PrimaryAccent, modifier = Modifier.size(28.dp))
                                                Spacer(Modifier.height(6.dp))
                                                Text("Files", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                            }
                                        }
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceCard).clickable { onRadioClick() }.padding(horizontal = 20.dp, vertical = 16.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Filled.Radio, null, tint = PrimaryAccent, modifier = Modifier.size(28.dp))
                                                Spacer(Modifier.height(6.dp))
                                                Text("Radio", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bookmarks section
                        if (!bookmarks.isNullOrEmpty()) {
                            item(key = "bookmarks") {
                                Box {
                                    Column {
                                        Spacer(Modifier.height(24.dp))
                                        Text("Bookmarks", color = TextPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 20.dp))
                                        Spacer(Modifier.height(8.dp))
                                        bookmarks.take(5).forEach { bm ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Icon(Icons.Filled.Star, null, tint = PrimaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(10.dp))
                                                bm.entry?.let { entry ->
                                                    Column(Modifier.weight(1f)) {
                                                        Text(entry.title, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text(entry.artist ?: "", style = MaterialTheme.typography.labelSmall, color = TextTertiary, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Now Playing section
                        if (!nowPlaying.isNullOrEmpty()) {
                            item(key = "nowplaying") {
                                Box {
                                    Column {
                                        Spacer(Modifier.height(24.dp))
                                        Text("Now Playing", color = TextPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 20.dp))
                                        Spacer(Modifier.height(8.dp))
                                        nowPlaying.take(5).forEach { np ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(np.title, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Row {
                                                        Text(np.artist ?: "", style = MaterialTheme.typography.labelSmall, color = TextTertiary, maxLines = 1)
                                                        np.username?.let { Text(" · $it", style = MaterialTheme.typography.labelSmall, color = TextTertiary) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Artists section
                        if (hasArtists) {
                            item(key = "artists") {
                                Spacer(modifier = Modifier.height(if (hasPlaylists || hasFavorites || hasGenres) 24.dp else 8.dp))
                                Text(
                                    text = stringResource(R.string.library_section_artists),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                                )
                            }

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
} // End LibraryScreenContent
}
