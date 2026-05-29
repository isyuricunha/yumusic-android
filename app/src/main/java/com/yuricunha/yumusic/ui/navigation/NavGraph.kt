package com.yuricunha.yumusic.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yuricunha.yumusic.ui.screens.album.AlbumScreen
import com.yuricunha.yumusic.ui.screens.artist.ArtistScreen
import com.yuricunha.yumusic.ui.screens.home.HomeScreen
import com.yuricunha.yumusic.ui.screens.library.LibraryScreen
import com.yuricunha.yumusic.ui.screens.player.PlayerScreen
import com.yuricunha.yumusic.ui.screens.search.SearchScreen
import com.yuricunha.yumusic.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Route.HOME,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(Route.HOME) {
                HomeScreen(
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
                    },
                    onSettingsClick = {
                        navController.navigate(Route.SETTINGS)
                    },
                )
            }

            composable(Route.LIBRARY) {
                LibraryScreen(
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
                    },
                )
            }

            composable(Route.SEARCH) {
                SearchScreen(
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
                    },
                    onAlbumClick = { albumId ->
                        navController.navigate(Route.album(albumId))
                    },
                )
            }

            composable(Route.SETTINGS) {
                SettingsScreen(
                    onConnected = {
                        navController.navigate(Route.HOME) {
                            popUpTo(Route.SETTINGS) { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route = Route.ARTIST,
                arguments = listOf(
                    navArgument("artistId") { type = NavType.StringType },
                ),
            ) {
                ArtistScreen(
                    onAlbumClick = { albumId ->
                        navController.navigate(Route.album(albumId))
                    },
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(
                route = Route.ALBUM,
                arguments = listOf(
                    navArgument("albumId") { type = NavType.StringType },
                ),
            ) {
                AlbumScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = {
                        navController.navigate(Route.PLAYER)
                    },
                )
            }

            composable(Route.PLAYER) {
                PlayerScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}
