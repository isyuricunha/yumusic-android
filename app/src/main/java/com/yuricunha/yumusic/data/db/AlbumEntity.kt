package com.yuricunha.yumusic.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String?,
    val artistId: String?,
    val coverArt: String?,
    val songCount: Int,
    val year: Int?,
)
