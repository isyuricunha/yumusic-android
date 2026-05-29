package com.yuricunha.yumusic.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE albumId = :albumId ORDER BY trackNumber ASC")
    fun getByAlbumId(albumId: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getById(id: String): TrackEntity?

    @Query("DELETE FROM tracks")
    suspend fun deleteAll()
}
