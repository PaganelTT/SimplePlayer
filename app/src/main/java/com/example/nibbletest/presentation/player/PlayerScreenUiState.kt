package com.example.nibbletest.presentation.player

import androidx.annotation.DrawableRes
import com.example.nibbletest.R

data class PlayerScreenUiState(
    @DrawableRes val bookImage: Int = R.drawable.img_book_cover,
    val chapterName: String = "",
    val currentSpeed: Float = 0f,
    val currentTime: Long = 0,
    val totalTime: Long = 0,
    val playerState: PlayerState = PlayerState.STOPPED,
    val isLoading: Boolean = true,
    val currentIndex: Int = -1,
    val chaptersCount: Int = 0,
)
