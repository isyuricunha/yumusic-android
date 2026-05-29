package com.yuricunha.yumusic.di

import android.content.Context
import androidx.room.Room
import com.yuricunha.yumusic.data.db.AlbumDao
import com.yuricunha.yumusic.data.db.AppDatabase
import com.yuricunha.yumusic.data.db.ArtistDao
import com.yuricunha.yumusic.data.db.PlaylistDao
import com.yuricunha.yumusic.data.db.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "yumusic.db",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideArtistDao(database: AppDatabase): ArtistDao = database.artistDao()

    @Provides
    fun provideAlbumDao(database: AppDatabase): AlbumDao = database.albumDao()

    @Provides
    fun provideTrackDao(database: AppDatabase): TrackDao = database.trackDao()

    @Provides
    fun providePlaylistDao(database: AppDatabase): PlaylistDao = database.playlistDao()
}
