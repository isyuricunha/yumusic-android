package com.yuricunha.yumusic.ui.screens.radio

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.InternetRadioStationDto
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioTabViewModel @Inject constructor(
    private val application: Application,
    private val repository: SubsonicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState<List<InternetRadioStationDto>>>(ScreenState.Loading)
    val uiState: StateFlow<ScreenState<List<InternetRadioStationDto>>> = _uiState.asStateFlow()

    private val _customStations = MutableStateFlow<List<CustomStation>>(emptyList())
    val customStations: StateFlow<List<CustomStation>> = _customStations.asStateFlow()

    init {
        loadStations()
        loadCustomStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _uiState.value = ScreenState.Loading
            repository.getInternetRadioStations()
                .onSuccess { _uiState.value = ScreenState.Success(it) }
                .onFailure { _uiState.value = ScreenState.Success(emptyList()) }
        }
    }

    private fun loadCustomStations() {
        // Read from SharedPreferences for simplicity
        val prefs = application.getSharedPreferences("radio_stations", 0)
        val count = prefs.getInt("count", 0)
        val stations = (0 until count).mapNotNull { i ->
            val name = prefs.getString("name_$i", null) ?: return@mapNotNull null
            val url = prefs.getString("url_$i", null) ?: return@mapNotNull null
            CustomStation(name, url)
        }
        _customStations.value = stations
    }

    fun addCustomStation(name: String, url: String) {
        val stations = _customStations.value + CustomStation(name, url)
        _customStations.value = stations

        // Save to SharedPreferences
        val prefs = application.getSharedPreferences("radio_stations", 0).edit()
        prefs.putInt("count", stations.size)
        stations.forEachIndexed { i, s ->
            prefs.putString("name_$i", s.name)
            prefs.putString("url_$i", s.streamUrl)
        }
        prefs.apply()
    }
}