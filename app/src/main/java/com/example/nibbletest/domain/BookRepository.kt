package com.example.nibbletest.domain

interface BookRepository  {
    suspend fun loadBook(): List<BookChapterModel>
}