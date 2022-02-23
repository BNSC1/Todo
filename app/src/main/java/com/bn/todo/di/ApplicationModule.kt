package com.bn.todo.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.bn.todo.data.db.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@SuppressLint("StaticFieldLeak")
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    lateinit var context: Context

    @Singleton
    @Provides
    fun provideTimber() = Timber.DebugTree()

    @Singleton
    @Provides
    fun provideDatabase() = synchronized(this) {
        Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todoDatabase"
        ).build()
    }
}