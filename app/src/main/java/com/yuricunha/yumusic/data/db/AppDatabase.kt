package com.yuricunha.yumusic.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArtistEntity::class,
        AlbumEntity::class,
        TrackEntity::class,
        PlaylistEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}
