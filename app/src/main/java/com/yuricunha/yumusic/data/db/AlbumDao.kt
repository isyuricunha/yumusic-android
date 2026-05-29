package com.yuricunha.yumusic.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("SELECT * FROM albums WHERE artistId = :artistId ORDER BY year ASC, name ASC")
    fun getByArtistId(artistId: String): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getById(id: String): AlbumEntity?

    @Query("DELETE FROM albums")
    suspend fun deleteAll()
}
