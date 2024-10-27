package com.example.nibbletest.domain

import kotlinx.coroutines.flow.MutableSharedFlow

interface PlayerController {

    fun addMediaItems(chapters: List<BookChapterModel>)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextChapter()

    fun skipToPreviousChapter()

    fun seekTo(position: Long)

    fun changeSpeed(currentSpeed: Float)

    val eventsFlow: MutableSharedFlow<PlayerEventsModel>

    fun getCurrentChapter(): BookChapterModel?
}