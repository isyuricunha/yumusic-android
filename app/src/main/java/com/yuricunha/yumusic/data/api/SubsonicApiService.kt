package com.yuricunha.yumusic.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicApiService {

    companion object {
        const val API_VERSION = "1.16.1"
        const val CLIENT_NAME = "yumusic"
    }

    @GET("rest/getArtists")
    suspend fun getArtists(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<ArtistsResponse>

    @GET("rest/getArtist")
    suspend fun getArtist(
        @Query("id") artistId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<ArtistResponse>

    @GET("rest/getAlbum")
    suspend fun getAlbum(
        @Query("id") albumId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<AlbumResponse>

    @GET("rest/search3")
    suspend fun search3(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int = 10,
        @Query("albumCount") albumCount: Int = 10,
        @Query("songCount") songCount: Int = 10,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<Search3Response>

    @GET("rest/getAlbumList2")
    suspend fun getAlbumList2(
        @Query("type") type: String = "alphabeticalByName",
        @Query("size") size: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<AlbumList2Response>

    @GET("rest/getPlaylists")
    suspend fun getPlaylists(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<PlaylistsResponse>

    @GET("rest/getPlaylist")
    suspend fun getPlaylist(
        @Query("id") playlistId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<PlaylistResponse>

    @GET("rest/star")
    suspend fun star(
        @Query("id") id: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    @GET("rest/unstar")
    suspend fun unstar(
        @Query("id") id: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    @GET("rest/getArtistInfo")
    suspend fun getArtistInfo(
        @Query("id") artistId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<ArtistInfoResponse>

    @GET("rest/getLyrics")
    suspend fun getLyrics(
        @Query("artist") artist: String,
        @Query("title") title: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<LyricsResponse>

    @GET("rest/getStarred")
    suspend fun getStarred(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<StarredResponse>

    // ── Genres ───────────────────────────────────────────────────────────

    @GET("rest/getGenres")
    suspend fun getGenres(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<GenresResponse>

    @GET("rest/getSongsByGenre")
    suspend fun getSongsByGenre(
        @Query("genre") genre: String,
        @Query("count") count: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SongsByGenreResponse>

    // ── Top Songs ────────────────────────────────────────────────────────

    @GET("rest/getTopSongs")
    suspend fun getTopSongs(
        @Query("artist") artist: String,
        @Query("count") count: Int = 10,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<TopSongsResponse>

    // ── Playlist management ──────────────────────────────────────────────

    @GET("rest/createPlaylist")
    suspend fun createPlaylist(
        @Query("name") name: String,
        @Query("songId") songId: List<String>? = null,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<PlaylistResponse>

    @GET("rest/updatePlaylist")
    suspend fun updatePlaylist(
        @Query("playlistId") playlistId: String,
        @Query("songIdToAdd") songIdToAdd: List<String>? = null,
        @Query("songIndexToRemove") songIndexToRemove: List<Int>? = null,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    @GET("rest/deletePlaylist")
    suspend fun deletePlaylist(
        @Query("id") playlistId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    // ── Scrobble ─────────────────────────────────────────────────────────

    @GET("rest/scrobble")
    suspend fun scrobble(
        @Query("id") trackId: String,
        @Query("submission") submission: Boolean = true,
        @Query("time") time: Long? = null,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    // ── Music Folders ────────────────────────────────────────────────────
    @GET("rest/getMusicFolders")
    suspend fun getMusicFolders(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<MusicFoldersResponse>

    // ── Folder / Directory browsing ──────────────────────────────────────
    @GET("rest/getMusicDirectory")
    suspend fun getMusicDirectory(
        @Query("id") dirId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<DirectoryResponse>

    // ── Bookmarks ────────────────────────────────────────────────────────
    @GET("rest/getBookmarks")
    suspend fun getBookmarks(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<BookmarksResponse>

    @GET("rest/createBookmark")
    suspend fun createBookmark(
        @Query("id") trackId: String,
        @Query("position") position: Long,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    @GET("rest/deleteBookmark")
    suspend fun deleteBookmark(
        @Query("id") trackId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SubsonicStatus>

    // ── Random Songs ─────────────────────────────────────────────────────
    @GET("rest/getRandomSongs")
    suspend fun getRandomSongs(
        @Query("size") size: Int = 10,
        @Query("genre") genre: String? = null,
        @Query("fromYear") fromYear: Int? = null,
        @Query("toYear") toYear: Int? = null,
        @Query("musicFolderId") musicFolderId: String? = null,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<RandomSongsResponse>

    // ── Similar Songs ────────────────────────────────────────────────────
    @GET("rest/getSimilarSongs2")
    suspend fun getSimilarSongs2(
        @Query("id") trackId: String,
        @Query("count") count: Int = 10,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<SimilarSongsResponse>

    // ── Now Playing ──────────────────────────────────────────────────────
    @GET("rest/getNowPlaying")
    suspend fun getNowPlaying(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<NowPlayingResponse>

    // ── Internet Radio ───────────────────────────────────────────────────
    @GET("rest/getInternetRadioStations")
    suspend fun getInternetRadioStations(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = API_VERSION,
        @Query("c") client: String = CLIENT_NAME,
        @Query("f") format: String = "json",
    ): SubsonicResponse<InternetRadioStationsResponse>
}
