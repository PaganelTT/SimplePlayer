package com.example.nibbletest.domain

import com.example.nibbletest.presentation.player.PlayerState

data class PlayerEventsModel(
    val playerState: PlayerState,
    val currentChapter: BookChapterModel?,
    val currentPosition: Long,
    val totalDuration: Long,
)