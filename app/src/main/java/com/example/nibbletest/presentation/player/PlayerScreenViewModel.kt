package com.example.nibbletest.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nibbletest.domain.BookChapterModel
import com.example.nibbletest.domain.BookRepository
import com.example.nibbletest.domain.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val bookRepository: BookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val speedList = listOf(1f, 1.25f, 1.5f, 1.75f, 2f, 0.5f, 0.75f)
    private var currentSpeed = speedList.first()
    private var bookChapters = emptyList<BookChapterModel>()
    private var selectedChapter: BookChapterModel? = null

    init {
        setMediaControllerCallback()
    }

    private fun setMediaControllerCallback() {
        viewModelScope.launch {
            playerController.eventsFlow.collect { event ->
                selectedChapter = event.currentChapter
                _uiState.update {
                    it.copy(
                        playerState = event.playerState,
                        chapterName = event.currentChapter?.title.orEmpty(),
                        totalTime = event.totalDuration,
                        currentTime = event.currentPosition,
                        currentSpeed = currentSpeed,
                        currentIndex = getCurrentIndex()
                    )
                }
                checkPlayerPosition(event.playerState)
            }
        }
    }

    private fun checkPlayerPosition(playerState: PlayerState) {
        if (playerState == PlayerState.PLAYING) {
            viewModelScope.launch {
                while (true) {
                    delay(1.seconds)
                    _uiState.update {
                        it.copy(
                            currentTime = playerController.getCurrentPosition()
                        )
                    }
                }
            }
        }
    }

    private fun getCurrentIndex() = selectedChapter?.let { bookChapters.indexOf(it) } ?: -1

    fun onUiEvent(event: PlayerScreenUiEvent) {
        when (event) {
            PlayerScreenUiEvent.OnPreviousChapterClick -> {
                playerController.skipToPreviousChapter()
            }

            PlayerScreenUiEvent.OnForwardClick -> {
                onSeek(playerController.getCurrentPosition() + 10 * 1000)
            }

            PlayerScreenUiEvent.OnNextChapterClick -> {
                playerController.skipToNextChapter()
            }

            PlayerScreenUiEvent.OnPlayPauseClick -> onPlayPause()
            PlayerScreenUiEvent.OnRewindClick -> onSeek(playerController.getCurrentPosition() - 5 * 1000)
            is PlayerScreenUiEvent.OnSeek -> onSeek(event.millis)
            PlayerScreenUiEvent.OnSpeedClick -> changeSpeed()
        }
    }

    private fun changeSpeed() {
        val newSpeedPosition = speedList.indexOf(currentSpeed) + 1
        currentSpeed = if (newSpeedPosition < speedList.size) {
            speedList[newSpeedPosition]
        } else {
            speedList.first()
        }
        playerController.changeSpeed(currentSpeed)
        _uiState.update { it.copy(currentSpeed = currentSpeed) }
    }

    private fun onSeek(seekTo: Long) {
        playerController.seekTo(seekTo)
    }

    private fun onPlayPause() {
        when (_uiState.value.playerState) {
            PlayerState.PLAYING -> playerController.pause()
            PlayerState.PAUSED -> playerController.resume()
            PlayerState.STOPPED -> playerController.play(getCurrentIndex())
        }
    }

    fun onCreated() {
        //TODO load book by another action?
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            bookChapters = bookRepository.loadBook()
            playerController.addMediaItems(bookChapters)
            _uiState.update { it.copy(isLoading = false, currentIndex = 0, chaptersCount = bookChapters.size) }
            playerController.play(0)
        }
    }

}