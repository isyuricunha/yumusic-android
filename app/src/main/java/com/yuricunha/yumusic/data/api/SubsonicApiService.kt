package com.yuricunha.yumusic.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicApiService {

    @GET("rest/getArtists")
    suspend fun getArtists(
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "yumusic",
        @Query("f") format: String = "json",
    ): SubsonicResponse<ArtistsResponse>

    @GET("rest/getArtist")
    suspend fun getArtist(
        @Query("id") artistId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "yumusic",
        @Query("f") format: String = "json",
    ): SubsonicResponse<ArtistResponse>

    @GET("rest/getAlbum")
    suspend fun getAlbum(
        @Query("id") albumId: String,
        @Query("u") username: String,
        @Query("p") password: String,
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "yumusic",
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
        @Query("v") version: String = "1.16.1",
        @Query("c") client: String = "yumusic",
        @Query("f") format: String = "json",
    ): SubsonicResponse<Search3Response>
}
