package com.bn.todo.ui.viewmodel

import app.cash.turbine.test
import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.repository.MockTodoRepository
import com.bn.todo.data.repository.MockUserPrefRepository
import com.bn.todo.data.repository.UserPrefRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class TodoViewModelTest {
    private lateinit var todoRepository: MockTodoRepository
    private lateinit var userPrefRepository: UserPrefRepository
    private lateinit var viewModel: TodoViewModel
    private val todoListName = "todoListName"

    @BeforeEach
    fun setup() {
        todoRepository = MockTodoRepository()
        userPrefRepository = MockUserPrefRepository()
        viewModel = TodoViewModel(todoRepository, userPrefRepository)
    }

    @Test
    fun `add todo list`() = runTest {
        val list = todoRepository.todoLists

        viewModel.insertTodoList(todoListName).join()

        list shouldHaveSize 1
    }

    @Test
    fun `delete todo list`() = runTest {
        val list = todoRepository.todoLists
        viewModel.insertTodoList(todoListName).join()
        list shouldHaveSize 1

        viewModel.deleteTodoList(list.first()).join()

        list shouldHaveSize 0
    }


    @Test
    fun `rename todo list`() = runTest {
        val newName = "newName"
        val list = todoRepository.todoLists
        viewModel.insertTodoList(todoListName).join()
        list shouldHaveSize 1

        viewModel.updateTodoList(list.first(), newName).join()

        list.first().name `should be equal to` newName
    }

    @Test
    fun `query todo lists`() = runTest {
        viewModel.insertTodoList(todoListName).join()
        viewModel.insertTodoList("list2").join()

        viewModel.todoLists.test {
            awaitItem()
            awaitItem() shouldHaveSize 2
        }
    }
}