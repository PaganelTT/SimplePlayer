package com.example.nibbletest.di

import android.content.Context
import com.example.nibbletest.data.repository.BookRepositoryImpl
import com.example.nibbletest.domain.BookRepository
import com.example.nibbletest.domain.PlayerController
import com.example.nibbletest.domain.PlayerControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePlayerController(@ApplicationContext context: Context): PlayerController =
        PlayerControllerImpl(context)

    @Singleton
    @Provides
    fun provideBookRepository(): BookRepository = BookRepositoryImpl()
}