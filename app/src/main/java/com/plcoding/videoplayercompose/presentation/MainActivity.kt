package com.plcoding.videoplayercompose.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.plcoding.videoplayercompose.presentation.components.ComposeVideoPlayer
import com.plcoding.videoplayercompose.ui.theme.VideoPlayerComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerComposeTheme {

                val viewModel = hiltViewModel<MainViewModel>()

                // recompose will trigger when state updates, we made it a compose state
                val videoItems by viewModel.videoItems.collectAsState()

                // Activity Launcher that fires when we want to select a video file
                val selectVideoLauncher = rememberLauncherForActivityResult(
                    // contract - what we want to do with this launcher
                    // in our case launch a new Activity for a specific result
                    // the new Activity will be the File Browser
                    contract = ActivityResultContracts.GetContent(),
                    // The result will be the chosen Video
                    onResult = { uri ->
                        // if not null add uri to viewModel
                        uri?.let(viewModel::addVideoUri)
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    ComposeVideoPlayer(viewModel)

                    Spacer(modifier = Modifier.height(8.dp))

                    IconButton(onClick = {
                        // Open File Browser Activity
                        // video/mp4 - to only show video files that end .mp4
                        selectVideoLauncher.launch("video/mp4")
                    }) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Select video"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // List of selected Videos
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(videoItems) { item ->
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.playVideo(item.contentUri)
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}