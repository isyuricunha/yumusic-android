package com.yuricunha.yumusic.data.api

import com.google.gson.annotations.SerializedName

// Generic wrapper
data class SubsonicResponse<T>(
    @SerializedName("subsonic-response") val response: T?,
)

data class SubsonicError(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
)

// ── getArtists ─────────────────────────────────────────────────────────────
// JSON: { "subsonic-response": { "status": "ok", "artists": { "index": [...] } } }
data class ArtistsResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("error") val error: SubsonicError?,
    @SerializedName("artists") val artists: ArtistIndexList?,
)

data class ArtistIndexList(
    @SerializedName("index") val indices: List<ArtistIndex>?,
)

data class ArtistIndex(
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artists: List<ArtistDto>?,
)

data class ArtistDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("albumCount") val albumCount: Int?,
    @SerializedName("coverArt") val coverArt: String?,
)

// ── getArtist (albums by artist) ───────────────────────────────────────────
// JSON: { "subsonic-response": { "status": "ok", "artist": { "id":"..", "album":[...] } } }
data class ArtistResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("error") val error: SubsonicError?,
    @SerializedName("artist") val artist: ArtistDetail?,
)

data class ArtistDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("album") val albums: List<AlbumDto>?,
)

// ── getAlbum (tracks by album) ─────────────────────────────────────────────
// JSON: { "subsonic-response": { "status": "ok", "album": { "id":"..", "song":[...] } } }
data class AlbumResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("error") val error: SubsonicError?,
    @SerializedName("album") val album: AlbumDetail?,
)

data class AlbumDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: String?,
    @SerializedName("artistId") val artistId: String?,
    @SerializedName("coverArt") val coverArt: String?,
    @SerializedName("song") val songs: List<TrackDto>?,
)

data class AlbumDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: String?,
    @SerializedName("artistId") val artistId: String?,
    @SerializedName("coverArt") val coverArt: String?,
    @SerializedName("songCount") val songCount: Int?,
    @SerializedName("year") val year: Int?,
)

data class TrackDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String?,
    @SerializedName("album") val album: String?,
    @SerializedName("albumId") val albumId: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("track") val trackNumber: Int?,
    @SerializedName("coverArt") val coverArt: String?,
)

// ── search3 ────────────────────────────────────────────────────────────────
// JSON: { "subsonic-response": { "status": "ok", "searchResult3": { "artist": [...], ... } } }
data class Search3Response(
    @SerializedName("status") val status: String?,
    @SerializedName("error") val error: SubsonicError?,
    @SerializedName("searchResult3") val searchResult: SearchResult3?,
)

data class SearchResult3(
    @SerializedName("artist") val artists: List<ArtistDto>?,
    @SerializedName("album") val albums: List<AlbumDto>?,
    @SerializedName("song") val songs: List<TrackDto>?,
)

// ── getAlbumList2 (random / recent / frequent albums) ──────────────────────
// JSON: { "subsonic-response": { "status": "ok", "albumList2": { "album": [...] } } }
data class AlbumList2Response(
    @SerializedName("status") val status: String?,
    @SerializedName("error") val error: SubsonicError?,
    @SerializedName("albumList2") val albumList: AlbumList2?,
)

data class AlbumList2(
    @SerializedName("album") val albums: List<AlbumDto>?,
)
