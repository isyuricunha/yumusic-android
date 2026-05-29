package com.yuricunha.yumusic.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val songCount: Int = 0,
    val duration: Int? = null,
    val owner: String? = null,
    val coverArt: String? = null,
    val isPublic: Boolean? = null,
)