package com.yuricunha.yumusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextTertiary

/**
 * Data required to render an item in [ArtistRow].
 */
data class ArtistRowItem(
    val id: String,
    val name: String,
    val coverArt: String?,
)

/**
 * Data required to render an item in [AlbumRow].
 */
data class AlbumRowItem(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
)

@Composable
fun ArtistRow(
    title: String,
    artists: List<ArtistRowItem>,
    coverArtUrl: (String?) -> String?,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(artists) { artist ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onArtistClick(artist.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AlbumArt(
                        coverArtUrl = coverArtUrl(artist.coverArt),
                        contentDescription = artist.name,
                        modifier = Modifier
                            .width(140.dp)
                            .height(140.dp),
                        cornerRadius = 70.dp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumRow(
    title: String,
    albums: List<AlbumRowItem>,
    coverArtUrl: (String?) -> String?,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(albums) { album ->
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { onAlbumClick(album.id) },
                ) {
                    AlbumArt(
                        coverArtUrl = coverArtUrl(album.coverArt),
                        contentDescription = album.name,
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        cornerRadius = 4.dp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!album.artist.isNullOrEmpty()) {
                        Text(
                            text = album.artist,
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
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Normal,
        ),
        color = TextPrimary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
