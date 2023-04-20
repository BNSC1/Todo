package com.bn.todo.di

import com.bn.todo.data.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import com.bn.todo.data.repository.FakeTodoRepository
import com.bn.todo.data.repository.FakeUserPrefRepository
import com.bn.todo.data.repository.UserPrefRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTodoRepository(repo: FakeTodoRepository): TodoRepository

    @Singleton
    @Binds
    abstract fun bindUserPrefRepository(repo: FakeUserPrefRepository): UserPrefRepository
}