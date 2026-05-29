package com.yuricunha.yumusic.ui.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.PlaylistDto
import com.yuricunha.yumusic.ui.components.AlbumArt
import com.yuricunha.yumusic.ui.theme.*
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onPlaylistClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.nav_playlists), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
        )

        when (uiState) {
            is ScreenState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryAccent) }
            is ScreenState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text((uiState as ScreenState.Error).message, color = TextTertiary) }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val playlists = (uiState as ScreenState.Success).data as? List<PlaylistDto>
                if (playlists.isNullOrEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No playlists", color = TextTertiary) }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(playlists) { pl ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onPlaylistClick(pl.id, pl.name) }.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Box(Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceCard), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.PlaylistPlay, null, tint = TextTertiary, modifier = Modifier.size(24.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(pl.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${pl.songCount ?: 0} tracks", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                }
                            }
                            HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(start = 82.dp))
                        }
                    }
                }
            }
        }
    }
}