package com.yuricunha.yumusic.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

object Route {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val SEARCH = "search"
    const val PLAYLISTS = "playlists"
    const val RADIO_TAB = "radio_tab"
    const val SETTINGS = "settings"
    const val ARTIST = "artist/{artistId}"
    const val ALBUM = "album/{albumId}"
    const val PLAYLIST = "playlist/{playlistId}/{playlistName}"
    const val GENRE = "genre/{genreName}"
    const val FOLDER = "folder/{folderId}/{folderName}"
    const val PLAYER = "player"

    fun artist(artistId: String) = "artist/$artistId"
    fun album(albumId: String) = "album/$albumId"
    fun playlist(playlistId: String, playlistName: String) = "playlist/$playlistId/$playlistName"
    fun genre(genreName: String) = "genre/$genreName"
    fun folder(folderId: String, folderName: String) = "folder/$folderId/$folderName"
}

data class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.HOME,
        labelResId = com.yuricunha.yumusic.R.string.nav_home,
        icon = Icons.Filled.Home,
    ),
    BottomNavItem(
        route = Route.PLAYLISTS,
        labelResId = com.yuricunha.yumusic.R.string.nav_playlists,
        icon = Icons.Filled.PlaylistPlay,
    ),
    BottomNavItem(
        route = Route.LIBRARY,
        labelResId = com.yuricunha.yumusic.R.string.nav_library,
        icon = Icons.Filled.LibraryMusic,
    ),
    BottomNavItem(
        route = Route.RADIO_TAB,
        labelResId = com.yuricunha.yumusic.R.string.nav_radio,
        icon = Icons.Filled.Radio,
    ),
    BottomNavItem(
        route = Route.SEARCH,
        labelResId = com.yuricunha.yumusic.R.string.nav_search,
        icon = Icons.Filled.Search,
    ),
)
