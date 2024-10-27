package com.example.nibbletest.presentation.player

sealed interface PlayerScreenUiEvent {
    data object OnSpeedClick : PlayerScreenUiEvent
    data object OnPreviousChapterClick : PlayerScreenUiEvent
    data object OnNextChapterClick : PlayerScreenUiEvent
    data object OnPlayPauseClick : PlayerScreenUiEvent
    data object OnRewindClick : PlayerScreenUiEvent
    data object OnForwardClick : PlayerScreenUiEvent
    data class OnSeek(val millis: Long) : PlayerScreenUiEvent
}