package com.yuricunha.yumusic.ui.screens.search.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.R
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.api.ArtistDto
import com.yuricunha.yumusic.data.api.TrackDto
import com.yuricunha.yumusic.data.repository.SettingsRepository
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResults(
    val artists: List<ArtistDto> = emptyList(),
    val albums: List<AlbumDto> = emptyList(),
    val songs: List<TrackDto> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val application: Application,
    private val repository: SubsonicRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState<SearchResults>>(ScreenState.Success(SearchResults()))
    val uiState: StateFlow<ScreenState<SearchResults>> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = ScreenState.Success(SearchResults())
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            val config = settingsRepository.serverConfig.first()
            if (!config.isConfigured) {
                _uiState.value = ScreenState.Error(application.getString(R.string.error_not_configured))
                return@launch
            }
            _uiState.value = ScreenState.Loading
            repository.search(query)
                .onSuccess { (artists, albums, songs) ->
                    _uiState.value = ScreenState.Success(
                        SearchResults(artists, albums, songs)
                    )
                }
                .onFailure { e ->
                    _uiState.value = ScreenState.Error(e.message ?: application.getString(R.string.error_not_configured))
                }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
