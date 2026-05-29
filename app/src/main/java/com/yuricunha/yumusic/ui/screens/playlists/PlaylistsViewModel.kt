package com.yuricunha.yumusic.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.PlaylistDto
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val repository: SubsonicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState<List<PlaylistDto>>>(ScreenState.Loading)
    val uiState: StateFlow<ScreenState<List<PlaylistDto>>> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ScreenState.Loading
            repository.getPlaylists()
                .onSuccess { _uiState.value = ScreenState.Success(it) }
                .onFailure { _uiState.value = ScreenState.Error(it.message ?: "Error") }
        }
    }
}