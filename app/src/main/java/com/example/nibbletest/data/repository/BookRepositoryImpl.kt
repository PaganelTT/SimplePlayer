package com.example.nibbletest.data.repository

import com.example.nibbletest.R
import com.example.nibbletest.domain.BookChapterModel
import com.example.nibbletest.domain.BookRepository
import com.example.nibbletest.other.resourceToUri
import kotlinx.coroutines.delay
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    override suspend fun loadBook(): List<BookChapterModel> {
        delay(1000)
        return listOf(
            BookChapterModel(R.raw.crazy.resourceToUri().toString(), "Crazy"),
            BookChapterModel(R.raw.everyday_normal_guy_2.resourceToUri().toString(), "Every day normal guy"),
            BookChapterModel(R.raw.lose_yourself.resourceToUri().toString(), "Lose yourself"),
            BookChapterModel(R.raw.till_i_collapse.resourceToUri().toString(), "Till I collapse"),
        )
    }
}