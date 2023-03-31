package com.bn.todo.usecase

import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.FakeTodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class UpdateTodoListUseCaseTest {
    private lateinit var updateTodoList: UpdateTodoListUseCase
    private lateinit var todoRepository: FakeTodoRepository
    private lateinit var lists: MutableList<TodoList>

    companion object {
        private const val todoListName = "todoListName"
    }

    @BeforeEach
    fun setup() {
        todoRepository = FakeTodoRepository()
        updateTodoList = UpdateTodoListUseCase(todoRepository)
        lists = todoRepository.todoLists
    }

    @Test
    fun `rename todo list empty name`() = runTest {
        val todoList = TodoList(todoListName, 1)
        lists.add(todoList)

        val result = coInvoking { updateTodoList(todoList,"") }

        result `should throw` IllegalArgumentException::class
        lists[0].name `should be equal to` todoListName
    }
}