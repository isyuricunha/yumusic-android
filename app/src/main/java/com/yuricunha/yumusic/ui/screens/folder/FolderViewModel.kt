package com.yuricunha.yumusic.ui.screens.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuricunha.yumusic.data.api.DirectoryChild
import com.yuricunha.yumusic.data.repository.SubsonicRepository
import com.yuricunha.yumusic.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderUiState(
    val folderName: String = "",
    val content: ScreenState<List<DirectoryChild>> = ScreenState.Loading,
)

@HiltViewModel
class FolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubsonicRepository,
) : ViewModel() {

    private val folderId: String = savedStateHandle["folderId"] ?: ""
    private val folderName: String = savedStateHandle["folderName"] ?: "Music"

    private val _uiState = MutableStateFlow(FolderUiState(folderName = folderName))
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    init { loadFolder() }

    fun loadFolder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(content = ScreenState.Loading)
            repository.getMusicDirectory(folderId)
                .onSuccess { dir ->
                    _uiState.value = FolderUiState(
                        folderName = dir.name,
                        content = ScreenState.Success(dir.children ?: emptyList()),
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(content = ScreenState.Error(e.message ?: "Error"))
                }
        }
    }

    fun getCoverArtUrl(coverArtId: String?): String? = if (coverArtId.isNullOrEmpty()) null else repository.getCoverArtUrl(coverArtId)
}