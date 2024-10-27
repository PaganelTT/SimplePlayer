package com.example.nibbletest.domain

import androidx.media3.common.MediaItem

data class BookChapterModel(
    val uri: String,
    val title: String
)

fun MediaItem.toBookChapter() =
    BookChapterModel(
        title = mediaMetadata.title.toString(),
        uri = mediaId,
    )
