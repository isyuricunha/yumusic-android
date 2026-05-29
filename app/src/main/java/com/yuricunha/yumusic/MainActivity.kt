package com.yuricunha.yumusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yuricunha.yumusic.data.repository.SettingsRepository
import com.yuricunha.yumusic.player.PlayerConnection
import com.yuricunha.yumusic.ui.components.MiniPlayer
import com.yuricunha.yumusic.ui.navigation.BottomNavBar
import com.yuricunha.yumusic.ui.navigation.NavGraph
import com.yuricunha.yumusic.ui.navigation.Route
import com.yuricunha.yumusic.ui.navigation.bottomNavItems
import com.yuricunha.yumusic.ui.screens.player.viewmodel.PlayerViewModel
import com.yuricunha.yumusic.ui.theme.Background
import com.yuricunha.yumusic.ui.theme.PrimaryAccent
import com.yuricunha.yumusic.ui.theme.YuMusicTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerConnection: PlayerConnection

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YuMusicTheme {
                YuMusicContent(settingsRepository)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            playerConnection.connect()
        } catch (_: Exception) {
            // Player service may not be available yet
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            playerConnection.disconnect()
        } catch (_: Exception) {
            // Ignore cleanup errors
        }
    }
}

@Composable
private fun YuMusicContent(
    settingsRepository: SettingsRepository,
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val isPlayerRoute = currentRoute == Route.PLAYER

    val playerState by playerViewModel.playerState.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            playerViewModel.connect()
        } catch (_: Exception) {
            // Player may not be ready
        }
        try {
            val config = settingsRepository.serverConfig.first()
            startDestination = if (config.isConfigured) Route.HOME else Route.SETTINGS
        } catch (_: Exception) {
            // Default to settings if config read fails
            startDestination = Route.SETTINGS
        }
    }

    val resolvedStartDestination = startDestination

    // Show loading state instead of blank screen
    if (resolvedStartDestination == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = PrimaryAccent)
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                if (playerState.isAvailable && playerState.title.isNotEmpty() && !isPlayerRoute) {
                    MiniPlayer(
                        title = playerState.title,
                        artist = playerState.artist,
                        coverArtUrl = playerState.coverArtUrl,
                        isPlaying = playerState.isPlaying,
                        progress = playerState.progress,
                        onPlayPauseClick = playerViewModel::playPause,
                        onNextClick = playerViewModel::skipToNext,
                        onClick = {
                            navController.navigate(Route.PLAYER)
                        },
                    )
                }
                if (showBottomBar) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute,
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavGraph(
                navController = navController,
                startDestination = resolvedStartDestination,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
