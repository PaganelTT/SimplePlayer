package com.example.nibbletest.presentation.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay5
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nibbletest.R
import com.example.nibbletest.other.formatMilliseconds

@Composable
fun PlayerScreen(viewModel: PlayerScreenViewModel = viewModel(), modifier: Modifier = Modifier) {

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.onCreated()
    }

    val uiState by viewModel.uiState.collectAsState()
    if (!uiState.isLoading) {
        Content(uiState, modifier, onUiEvent = viewModel::onUiEvent)
    }
}

@Composable
fun Content(uiState: PlayerScreenUiState, modifier: Modifier = Modifier, onUiEvent: (PlayerScreenUiEvent) -> Unit) {
    Column(modifier = modifier.fillMaxSize().padding(top = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        BookCover(
            uiState.bookImage,
            Modifier
                .fillMaxWidth(fraction = 0.5f)
                .aspectRatio(0.5f)
        )
        AnimatedVisibility(uiState.currentIndex >= 0) {
            ChapterInfo(uiState.currentIndex, uiState.chaptersCount, uiState.chapterName, modifier = Modifier.fillMaxWidth())
        }
        Progress(Modifier.fillMaxWidth(), uiState.currentTime, uiState.totalTime, onUiEvent)
        Spacer(Modifier.height(16.dp))
        SpeedIndicator(uiState.currentSpeed, onUiEvent)
        Spacer(Modifier.height(32.dp))
        PlayerControls(onUiEvent, uiState.playerState, uiState.currentIndex, uiState.chaptersCount, Modifier.fillMaxWidth())
    }
}

@Composable
private fun BookCover(bookImage: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(bookImage),
        contentScale = ContentScale.Crop,
        contentDescription = "Book image",
        modifier = modifier
    )
}

@Composable
private fun ChapterInfo(currentIndex: Int, chaptersCount: Int, chapterName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.chapter_info, currentIndex + 1, chaptersCount).uppercase(), style = MaterialTheme.typography.subtitle1)
        Text(chapterName, style = MaterialTheme.typography.h6)
    }
}

@Composable
private fun Progress(
    modifier: Modifier = Modifier,
    currentTime: Long, totalTime: Long,
    onUiEvent: (PlayerScreenUiEvent) -> Unit,
) {
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(currentTime.formatMilliseconds(), style = MaterialTheme.typography.caption)
        Slider(
            modifier = Modifier.weight(1f),
            value = currentTime.toFloat(),
            valueRange = 0f..totalTime.toFloat(),
            onValueChange = {
                onUiEvent(PlayerScreenUiEvent.OnSeek(it.toLong()))
            },
        )
        Text(totalTime.formatMilliseconds(), style = MaterialTheme.typography.caption)
    }
}

@Composable
private fun SpeedIndicator(currentSpeed: Float, onUiEvent: (PlayerScreenUiEvent) -> Unit) {
    Button(onClick = { onUiEvent(PlayerScreenUiEvent.OnSpeedClick) }) {
        Text(stringResource(R.string.speed_indicator, currentSpeed))
    }
}

@Composable
private fun PlayerControls(
    onUiEvent: (PlayerScreenUiEvent) -> Unit,
    playerState: PlayerState,
    currentIndex: Int,
    chaptersCount: Int,
    modifier: Modifier,
) {
    val playPauseIcon =
        if (playerState == PlayerState.PLAYING) Icons.Filled.Pause else Icons.Filled.PlayArrow
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.SkipPrevious,
            contentDescription = "Skip Previous",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = currentIndex in 1..<chaptersCount, onClick = { onUiEvent(PlayerScreenUiEvent.OnPreviousChapterClick) })
                .size(32.dp)
        )
        Icon(
            imageVector = Icons.Rounded.Replay5,
            contentDescription = "Replay 5 seconds",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = { onUiEvent(PlayerScreenUiEvent.OnRewindClick) })
                .size(32.dp)
        )
        Icon(
            imageVector = playPauseIcon,
            contentDescription = "PlayPause",
            tint = MaterialTheme.colors.background,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colors.onBackground)
                .clickable(onClick = { onUiEvent(PlayerScreenUiEvent.OnPlayPauseClick) })
                .size(32.dp)
        )
        Icon(
            imageVector = Icons.Rounded.Forward10,
            contentDescription = "Forward 10 seconds",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = { onUiEvent(PlayerScreenUiEvent.OnForwardClick) })
                .size(32.dp)
        )
        Icon(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "Skip Next",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = currentIndex in 0..<chaptersCount - 1, onClick = { onUiEvent(PlayerScreenUiEvent.OnNextChapterClick) })
                .size(32.dp)
        )
    }
}

