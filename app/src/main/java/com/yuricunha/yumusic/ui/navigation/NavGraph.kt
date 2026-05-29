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
import com.yuricunha.yumusic.ui.screens.folder.FolderScreen
import com.yuricunha.yumusic.ui.screens.genre.GenreSongsScreen
import com.yuricunha.yumusic.ui.screens.home.HomeScreen
import com.yuricunha.yumusic.ui.screens.library.LibraryScreen
import com.yuricunha.yumusic.ui.screens.player.PlayerScreen
import com.yuricunha.yumusic.ui.screens.playlist.PlaylistScreen
import com.yuricunha.yumusic.ui.screens.playlists.PlaylistsScreen
import com.yuricunha.yumusic.ui.screens.radio.RadioTabScreen
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
                    onAlbumClick = { albumId ->
                        navController.navigate(Route.album(albumId))
                    },
                    onSettingsClick = {
                        navController.navigate(Route.SETTINGS)
                    },
                    onFolderClick = { folderId, folderName ->
                        navController.navigate(Route.folder(folderId, folderName))
                    },
                )
            }

            composable(Route.PLAYLISTS) {
                PlaylistsScreen(
                    onPlaylistClick = { playlistId, playlistName ->
                        navController.navigate(Route.playlist(playlistId, playlistName))
                    },
                )
            }

            composable(Route.LIBRARY) {
                LibraryScreen(
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
                    },
                    onPlaylistClick = { playlistId, playlistName ->
                        navController.navigate(Route.playlist(playlistId, playlistName))
                    },
                    onGenreClick = { genreName ->
                        navController.navigate(Route.genre(genreName))
                    },
                )
            }

            composable(Route.RADIO_TAB) {
                RadioTabScreen(
                    onPlayStation = { name, url ->
                        // Play station via PlayerConnection
                        navController.navigate(Route.PLAYER)
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
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
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
                    onArtistClick = { artistId ->
                        navController.navigate(Route.artist(artistId))
                    },
                )
            }

            composable(Route.PLAYER) {
                PlayerScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(
                route = Route.PLAYLIST,
                arguments = listOf(
                    navArgument("playlistId") { type = NavType.StringType },
                    navArgument("playlistName") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                PlaylistScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = {
                        navController.navigate(Route.PLAYER)
                    },
                )
            }

            composable(
                route = Route.GENRE,
                arguments = listOf(
                    navArgument("genreName") { type = NavType.StringType },
                ),
            ) {
                GenreSongsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = {
                        navController.navigate(Route.PLAYER)
                    },
                )
            }
        }
    }
}
