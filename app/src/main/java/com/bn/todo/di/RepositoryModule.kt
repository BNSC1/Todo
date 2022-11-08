package com.bn.todo.di

import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.TodoRepositoryImpl
import com.bn.todo.data.repository.UserPrefRepository
import com.bn.todo.data.repository.UserPrefRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserPrefRepository(repositoryImpl: UserPrefRepositoryImpl): UserPrefRepository

    @Binds
    @Singleton
    abstract fun bindTodoRepository(repositoryImpl: TodoRepositoryImpl): TodoRepository
}