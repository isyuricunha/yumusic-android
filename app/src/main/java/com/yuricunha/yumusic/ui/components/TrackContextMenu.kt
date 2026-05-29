package com.yuricunha.yumusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuricunha.yumusic.ui.theme.Divider
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary

data class TrackAction(
    val icon: ImageVector,
    val label: String,
    val action: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContextMenu(
    trackTitle: String,
    trackArtist: String?,
    isStarred: Boolean,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onGoToArtist: (() -> Unit)? = null,
    onGoToAlbum: (() -> Unit)? = null,
    onToggleStar: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = com.yuricunha.yumusic.ui.theme.BackgroundElevated,
        contentColor = TextPrimary,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            // Track info header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = trackTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    trackArtist?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Divider, thickness = 0.5.dp)

            // Actions
            ContextMenuItem(icon = Icons.Filled.SkipNext, label = "Play next", onClick = { onDismiss(); onPlayNext() })
            ContextMenuItem(icon = Icons.Filled.QueueMusic, label = "Add to queue", onClick = { onDismiss(); onAddToQueue() })
            ContextMenuItem(icon = Icons.Filled.PlaylistAdd, label = "Add to playlist", onClick = { onDismiss(); onAddToPlaylist() })
            if (onGoToArtist != null) {
                ContextMenuItem(icon = Icons.Filled.Person, label = "Go to artist", onClick = { onDismiss(); onGoToArtist() })
            }
            if (onGoToAlbum != null) {
                ContextMenuItem(icon = Icons.Filled.Album, label = "Go to album", onClick = { onDismiss(); onGoToAlbum() })
            }
            ContextMenuItem(
                icon = if (isStarred) Icons.Filled.Star else Icons.Filled.StarBorder,
                label = if (isStarred) "Remove from favorites" else "Add to favorites",
                tint = if (isStarred) PrimaryAccent else TextPrimary,
                onClick = { onDismiss(); onToggleStar() },
            )
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = TextPrimary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
    }
}