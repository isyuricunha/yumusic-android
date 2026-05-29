package com.yuricunha.yumusic.ui.screens.genre

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
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSongsScreen(
    onBackClick: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenreSongsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.genreName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
        )

        when (uiState.tracks) {
            is ScreenState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            }
            is ScreenState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (uiState.tracks as ScreenState.Error).message, color = TextTertiary)
                }
            }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val tracks = (uiState.tracks as ScreenState.Success).data as? List<*>
                if (tracks.isNullOrEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No songs found", color = TextTertiary)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(tracks) { index, track ->
                            val t = track as com.yuricunha.yumusic.data.api.TrackDto
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.playTrack(index) }
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
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = t.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    t.artist?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextTertiary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                                val dur = t.duration ?: 0
                                Text(
                                    text = stringResource(R.string.album_track_duration, dur / 60, dur % 60),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary,
                                )
                            }
                            if (index < tracks.size - 1) {
                                HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(start = 58.dp))
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}