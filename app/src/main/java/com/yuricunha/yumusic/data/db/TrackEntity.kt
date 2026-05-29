package com.yuricunha.yumusic.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [Index(value = ["albumId"])],
)
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val albumId: String?,
    val duration: Int?,
    val trackNumber: Int?,
    val coverArt: String?,
    val starred: String?,
)
