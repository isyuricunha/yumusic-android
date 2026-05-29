package com.yuricunha.yumusic.ui.screens.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.InternetRadioStationDto
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.player.PlayerConnection
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: SubsonicRepository,
    private val playerConnection: PlayerConnection,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState<List<InternetRadioStationDto>>>(ScreenState.Loading)
    val uiState: StateFlow<ScreenState<List<InternetRadioStationDto>>> = _uiState.asStateFlow()

    init { loadStations() }

    fun loadStations() {
        viewModelScope.launch {
            _uiState.value = ScreenState.Loading
            repository.getInternetRadioStations()
                .onSuccess { _uiState.value = ScreenState.Success(it) }
                .onFailure { _uiState.value = ScreenState.Error(it.message ?: "Error loading stations") }
        }
    }

    fun playStation(station: InternetRadioStationDto) {
        viewModelScope.launch {
            val mediaItem = playerConnection.buildMediaItem(
                streamUrl = station.streamUrl,
                id = station.id,
                title = station.name,
                artist = "Internet Radio",
                album = null,
                coverArtUrl = null,
            )
            playerConnection.play(listOf(mediaItem), 0)
        }
    }
}