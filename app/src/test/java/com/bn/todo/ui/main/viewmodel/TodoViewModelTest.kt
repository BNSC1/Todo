package com.bn.todo.ui.main.viewmodel

import app.cash.turbine.test
import com.bn.todo.MainCoroutineExtension
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.TodoList
import com.bn.todo.usecase.GetTodoListFlowUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class TodoViewModelTest {
    private lateinit var viewModel: TodoListViewModel
    private val mockGetTodoListFlow = mockk<GetTodoListFlowUseCase>()
    private val todoList = TodoList(todoListName)

    companion object {
        private const val todoListName = "todoListName"
    }

    @Test
    fun `delete last todo list`() = runTest {
        every { mockGetTodoListFlow(null) } returns flowOf(listOf(todoList))
        viewModel = TodoListViewModel(
            mockk(),
            mockGetTodoListFlow,
            mockk(),
            mockk(),
            mockk(relaxed = true),
            mockk()
        )

        viewModel.message.test {
            viewModel.deleteTodoList(todoList)

            awaitItem() `should be instance of` ViewModelMessage.Error::class
        }
    }
}