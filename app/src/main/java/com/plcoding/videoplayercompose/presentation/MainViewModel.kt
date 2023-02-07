package com.plcoding.videoplayercompose.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.plcoding.videoplayercompose.domain.MetaDataReader
import com.plcoding.videoplayercompose.domain.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // bundle of data that survive process death
    private val savedStateHandle: SavedStateHandle,
    // functionality to change video playback - play/pause video, next video
    // set specific media item list we want to play (playList)
    val player: Player,
    private val metaDataReader: MetaDataReader
): ViewModel() {

    // list of Uris
    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    val videoItems = videoUris.map { uris ->
        // map Uris to VideoItems
        uris.map { uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                // Parsing the names of content Uris to get the File Name
                name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No name"
            )
        }
        // make it a State Flow
        // SharingStarted.WhileSubscribed() - the block above will execute only with subscribers
        // 5000 - for how long it will keep the executing the code after the last subscriber disappears7
        // emptyList() - initial value
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // mandatory to call first
        // if want to play videos or music
        player.prepare()
    }

    // When we open file browser and select a video
    // call this function to add it to our Uris list
    // and display it in UI
    fun addVideoUri(uri: Uri) {
        savedStateHandle["videoUris"] = videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(
            videoItems.value.find { it.contentUri == uri }?.mediaItem ?: return
        )
    }

    // When the viewModel is cleared (popped from the backstack)
    override fun onCleared() {
        super.onCleared()
        // release video resource
        player.release()
    }
}