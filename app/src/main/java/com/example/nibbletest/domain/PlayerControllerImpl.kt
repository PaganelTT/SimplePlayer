package com.example.nibbletest.domain

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.nibbletest.R
import com.example.nibbletest.data.service.PlayerService
import com.example.nibbletest.other.resourceToUri
import com.example.nibbletest.presentation.player.PlayerState
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableSharedFlow

class PlayerControllerImpl(context: Context) : PlayerController {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override val eventsFlow: MutableSharedFlow<PlayerEventsModel> = MutableSharedFlow(replay = 1)

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
    }

    private val controllerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            with(player) {
                eventsFlow.tryEmit(
                    PlayerEventsModel(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.toBookChapter(),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L)
                    )
                )
            }
        }
    }

    private fun controllerListener() {
        mediaController?.addListener(controllerListener)
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    override fun addMediaItems(chapters: List<BookChapterModel>) {
        val mediaItems = chapters.map {
            MediaItem.Builder()
                .setMediaId(it.uri)
                .setUri(it.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtworkUri(R.drawable.img_book_cover.resourceToUri())
                        .build()
                )
                .build()
        }

        mediaController?.setMediaItems(mediaItems)
    }

    override fun play(mediaItemIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(mediaItemIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun getCurrentChapter(): BookChapterModel? = mediaController?.currentMediaItem?.toBookChapter()


    override fun changeSpeed(currentSpeed: Float) {
        mediaController?.setPlaybackSpeed(currentSpeed)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaController?.removeListener(controllerListener)
    }

    override fun skipToNextChapter() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousChapter() {
        mediaController?.seekToPrevious()
    }
}