package com.plcoding.videoplayercompose.presentation.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.plcoding.videoplayercompose.presentation.MainViewModel

@Composable
fun ComposeVideoPlayer(
    viewModel: MainViewModel
) {

    // LifeCycle State
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // whenever the key (lifecycleOwner) changes
    // the effect block {} executes
    DisposableEffect(lifecycleOwner) {

        // This will be called for every single LifeCycle Event
        val observer = LifecycleEventObserver { _, event ->
            // update the last event
            lifecycle = event
        }

        // Register the observer
        lifecycleOwner.lifecycle.addObserver(observer)

        // remove the observer when this screen leaves the composition
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // To view XML views in a Composable
    AndroidView(
        factory = { context ->
            PlayerView(context).also {
                // link player from viewMOdel to the view player
                it.player = viewModel.player
            }
        },
        // update block is called when state changes
        // that means the state that is inside the update block
        update = {
            when (lifecycle) {
                Lifecycle.Event.ON_PAUSE -> {
                    // when lifecycle is paused the video will pause
                    it.onPause()
                    it.player?.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    it.onResume()
                }
                else -> Unit
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            // TODO Adjust player to better view in Landscape
            .aspectRatio(16 / 9f)
    )
}