package com.yuricunha.yumusic.data.repository

import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.api.SubsonicApiService
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.data.db.AlbumDao
import com.yuricunha.yumusic.data.db.AlbumEntity
import com.yuricunha.yumusic.data.db.ArtistDao
import com.yuricunha.yumusic.data.db.ArtistEntity
import com.yuricunha.yumusic.data.db.TrackDao
import com.yuricunha.yumusic.data.db.TrackEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubsonicRepository @Inject constructor(
    private val apiService: SubsonicApiService,
    private val settingsRepository: SettingsRepository,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
) {
    private var cachedConfig: ServerConfig? = null

    private suspend fun getConfig(): ServerConfig {
        val config = settingsRepository.serverConfig.first()
        cachedConfig = config
        return config
    }

    private fun getCachedConfig(): ServerConfig? = cachedConfig

    private fun buildBaseUrl(config: ServerConfig): String =
        config.serverUrl.trimEnd('/')

    suspend fun getArtists(): Result<List<ArtistDto>> {
        val config = getConfig()
        if (!config.isConfigured) return Result.failure(IllegalStateException("Server not configured"))
        return try {
            val response = apiService.getArtists(
                username = config.username,
                password = config.password,
            )
            val error = response.response?.error
            if (error != null) return Result.failure(Exception(error.message))
            val artists = response.response?.artists?.indices?.flatMap { it.artists ?: emptyList() }
                ?: emptyList()
            // Cache to Room
            artistDao.deleteAll()
            artistDao.insertAll(artists.map { it.toEntity() })
            Result.success(artists)
        } catch (e: Exception) {
            // Fall back to cache
            val cached = artistDao.getAll().first()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getAlbumsByArtist(artistId: String): Result<Pair<String, List<AlbumDto>>> {
        val config = getConfig()
        if (!config.isConfigured) return Result.failure(IllegalStateException("Server not configured"))
        return try {
            val response = apiService.getArtist(
                artistId = artistId,
                username = config.username,
                password = config.password,
            )
            val error = response.response?.error
            if (error != null) return Result.failure(Exception(error.message))
            val artistDetail = response.response?.artist
            val albums = artistDetail?.albums ?: emptyList()
            val artistName = artistDetail?.name ?: albums.firstOrNull()?.artist ?: ""
            // Cache to Room
            albumDao.insertAll(albums.map { it.toEntity() })
            Result.success(Pair(artistName, albums))
        } catch (e: Exception) {
            val cached = albumDao.getByArtistId(artistId).first()
            if (cached.isNotEmpty()) {
                val name = cached.firstOrNull()?.artist ?: ""
                Result.success(Pair(name, cached.map { it.toDto() }))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getTracksByAlbum(albumId: String): Result<List<TrackDto>> {
        val config = getConfig()
        if (!config.isConfigured) return Result.failure(IllegalStateException("Server not configured"))
        return try {
            val response = apiService.getAlbum(
                albumId = albumId,
                username = config.username,
                password = config.password,
            )
            val error = response.response?.error
            if (error != null) return Result.failure(Exception(error.message))
            val songs = response.response?.album?.songs ?: emptyList()
            // Cache to Room
            trackDao.insertAll(songs.map { it.toEntity() })
            Result.success(songs)
        } catch (e: Exception) {
            val cached = trackDao.getByAlbumId(albumId).first()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun search(query: String): Result<Triple<List<ArtistDto>, List<AlbumDto>, List<TrackDto>>> {
        val config = getConfig()
        if (!config.isConfigured) return Result.failure(IllegalStateException("Server not configured"))
        return try {
            val response = apiService.search3(
                query = query,
                username = config.username,
                password = config.password,
            )
            val error = response.response?.error
            if (error != null) return Result.failure(Exception(error.message))
            val result = response.response?.searchResult
            Result.success(
                Triple(
                    result?.artists ?: emptyList(),
                    result?.albums ?: emptyList(),
                    result?.songs ?: emptyList(),
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomAlbums(size: Int = 20): Result<List<AlbumDto>> {
        val config = getConfig()
        if (!config.isConfigured) return Result.failure(IllegalStateException("Server not configured"))
        return try {
            val response = apiService.getAlbumList2(
                type = "random",
                size = size,
                username = config.username,
                password = config.password,
            )
            val error = response.response?.error
            if (error != null) return Result.failure(Exception(error.message))
            Result.success(response.response?.albumList?.albums ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getStreamUrl(trackId: String): String {
        val config = getCachedConfig() ?: return "http://placeholder.example.com/rest/stream?id=$trackId"
        return "${buildBaseUrl(config)}/rest/stream?id=$trackId"
    }

    fun getCoverArtUrl(coverArtId: String): String {
        val config = getCachedConfig() ?: return ""
        return "${buildBaseUrl(config)}/rest/getCoverArt?id=$coverArtId"
    }

    // --- Entity mapping ---

    private fun ArtistDto.toEntity() = ArtistEntity(
        id = id,
        name = name,
        albumCount = albumCount ?: 0,
        coverArt = coverArt,
    )

    private fun ArtistEntity.toDto() = ArtistDto(
        id = id,
        name = name,
        albumCount = albumCount,
        coverArt = coverArt,
    )

    private fun AlbumDto.toEntity() = AlbumEntity(
        id = id,
        name = name,
        artist = artist,
        artistId = artistId,
        coverArt = coverArt,
        songCount = songCount ?: 0,
        year = year,
    )

    private fun AlbumEntity.toDto() = AlbumDto(
        id = id,
        name = name,
        artist = artist,
        artistId = artistId,
        coverArt = coverArt,
        songCount = songCount,
        year = year,
    )

    private fun TrackDto.toEntity() = TrackEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        duration = duration,
        trackNumber = trackNumber,
        coverArt = coverArt,
    )

    private fun TrackEntity.toDto() = TrackDto(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumId = albumId,
        duration = duration,
        trackNumber = trackNumber,
        coverArt = coverArt,
    )
}
