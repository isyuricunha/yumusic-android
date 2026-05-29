package com.yuricunha.yumusic.ui.screens.folder

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary
import com.yuricunha.yumusic.util.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    onFolderClick: (String, String) -> Unit = { _, _ -> },
    onTrackClick: (String) -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FolderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.folderName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back), tint = TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
        )

        when (uiState.content) {
            is ScreenState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            }
            is ScreenState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (uiState.content as ScreenState.Error).message, color = TextTertiary)
                }
            }
            is ScreenState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val children = (uiState.content as ScreenState.Success).data as? List<*>
                if (children.isNullOrEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Empty folder", color = TextTertiary)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(children) { child ->
                            val isDir = child is com.yuricunha.yumusic.data.api.DirectoryChild && child.isDir
                            val title = if (child is com.yuricunha.yumusic.data.api.DirectoryChild) child.title ?: child.id else child.toString()
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isDir && child is com.yuricunha.yumusic.data.api.DirectoryChild) {
                                            onFolderClick(child.id, child.title ?: child.id)
                                        } else if (!isDir && child is com.yuricunha.yumusic.data.api.DirectoryChild) {
                                            onTrackClick(child.id)
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isDir) Icons.Filled.Folder else Icons.Filled.MusicNote,
                                        contentDescription = null,
                                        tint = if (isDir) PrimaryAccent else TextTertiary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        val displayTitle = if (child is com.yuricunha.yumusic.data.api.DirectoryChild) child.title ?: child.id else child.toString()
                                        Text(
                                            text = displayTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        if (!isDir && child is com.yuricunha.yumusic.data.api.DirectoryChild) {
                                            child.artist?.let {
                                                Text(text = it, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                            }
                                        }
                                    }
                                }
                            }
                            if (children.indexOf(child) < children.size - 1) {
                                HorizontalDivider(color = Divider, thickness = 0.5.dp, modifier = Modifier.padding(start = 52.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}