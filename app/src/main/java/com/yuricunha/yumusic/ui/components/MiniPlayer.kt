package com.yuricunha.yumusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.ui.theme.BackgroundElevated
import com.yuricunha.yumusic.ui.theme.HoverRipple
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextSecondary
import com.yuricunha.yumusic.ui.theme.TextTertiary

@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    coverArtUrl: String?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundElevated)
            .clickable { onClick() },
    ) {
        // Ultra-thin progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp),
            color = PrimaryAccent.copy(alpha = 0.6f),
            trackColor = HoverRipple,
            strokeCap = StrokeCap.Round,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art
            AlbumArt(
                coverArtUrl = coverArtUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp)),
                cornerRadius = 4.dp,
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Track info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.ifEmpty { stringResource(R.string.mini_player_no_track) },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                )
                if (artist.isNotEmpty()) {
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextTertiary,
                    )
                }
            }

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) {
                            stringResource(R.string.mini_player_pause)
                        } else {
                            stringResource(R.string.mini_player_play)
                        },
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                IconButton(onClick = onNextClick) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = stringResource(R.string.mini_player_next),
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}