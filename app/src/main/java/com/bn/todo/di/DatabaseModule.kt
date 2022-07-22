package com.bn.todo.di

import android.content.Context
import androidx.room.Room
import com.bn.todo.data.db.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = synchronized(this) {
        Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todoDatabase"
        ).build()
    }

    @Singleton
    @Provides
    fun provideTodoDao(database: TodoDatabase) = database.todoDao()

    @Singleton
    @Provides
    fun provideTodoListDao(database: TodoDatabase) = database.todoListDao()
}