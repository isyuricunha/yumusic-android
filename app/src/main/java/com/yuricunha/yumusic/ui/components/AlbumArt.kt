package com.yuricunha.yumusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.yuricunha.yumusic.ui.theme.HoverRipple
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.TextTertiary

@Composable
fun AlbumArt(
    coverArtUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(HoverRipple),
        contentAlignment = Alignment.Center,
    ) {
        if (!coverArtUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = coverArtUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    CircularProgressIndicator(
                        color = PrimaryAccent,
                        modifier = Modifier.align(Alignment.Center),
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                },
            )
        } else {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = contentDescription,
                tint = TextTertiary,
            )
        }
    }
}
