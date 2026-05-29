package com.yuricunha.yumusic.ui.screens.artist.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.AlbumDto
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistUiState(
    val artistName: String = "",
    val albums: ScreenState<List<AlbumDto>> = ScreenState.Loading,
)

@HiltViewModel
class ArtistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubsonicRepository,
) : ViewModel() {

    private val artistId: String = savedStateHandle["artistId"] ?: ""

    private val _uiState = MutableStateFlow(ArtistUiState())
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(albums = ScreenState.Loading)
            repository.getAlbumsByArtist(artistId)
                .onSuccess { (name, albums) ->
                    _uiState.value = _uiState.value.copy(
                        artistName = name,
                        albums = ScreenState.Success(albums),
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        albums = ScreenState.Error(e.message ?: "Unknown error"),
                    )
                }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? {
        if (coverArtId.isNullOrEmpty()) return null
        return repository.getCoverArtUrl(coverArtId)
    }
}
