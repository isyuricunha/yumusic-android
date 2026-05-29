package com.yuricunha.yumusic.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(artists: List<ArtistEntity>)

    @Query("SELECT * FROM artists ORDER BY name ASC")
    fun getAll(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getById(id: String): ArtistEntity?

    @Query("DELETE FROM artists")
    suspend fun deleteAll()
}
