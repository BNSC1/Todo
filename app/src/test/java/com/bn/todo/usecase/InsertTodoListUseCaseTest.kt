package com.bn.todo.usecase

import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.MockTodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class InsertTodoListUseCaseTest {
    private lateinit var insertTodoList: InsertTodoListUseCase
    private lateinit var todoRepository: MockTodoRepository
    private lateinit var lists: List<TodoList>

    @BeforeEach
    fun setup() {
        todoRepository = MockTodoRepository()
        insertTodoList = InsertTodoListUseCase(todoRepository)
        lists = todoRepository.todoLists
    }

    @Test
    fun `add todo list with empty name`() = runTest {

        val result = coInvoking { insertTodoList("") }

        result `should throw` IllegalArgumentException::class
        lists shouldHaveSize 0
    }
}