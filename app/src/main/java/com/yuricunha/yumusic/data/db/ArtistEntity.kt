package com.yuricunha.yumusic.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val albumCount: Int,
    val coverArt: String?,
)
