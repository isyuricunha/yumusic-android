package com.yuricunha.yumusic.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

object Route {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val ARTIST = "artist/{artistId}"
    const val ALBUM = "album/{albumId}"
    const val PLAYER = "player"

    fun artist(artistId: String) = "artist/$artistId"
    fun album(albumId: String) = "album/$albumId"
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
        route = Route.LIBRARY,
        labelResId = com.yuricunha.yumusic.R.string.nav_library,
        icon = Icons.Filled.LibraryMusic,
    ),
    BottomNavItem(
        route = Route.SEARCH,
        labelResId = com.yuricunha.yumusic.R.string.nav_search,
        icon = Icons.Filled.Search,
    ),
)
