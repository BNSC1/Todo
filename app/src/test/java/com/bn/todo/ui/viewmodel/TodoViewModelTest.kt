package com.bn.todo.ui.viewmodel

import app.cash.turbine.test
import com.bn.todo.MainCoroutineExtension
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.repository.MockTodoRepository
import com.bn.todo.data.repository.MockUserPrefRepository
import com.bn.todo.data.repository.UserPrefRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should not contain`
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
    private val todoName = "todoName"

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
        viewModel.currentList.test {
            awaitItem()
            awaitItem() `should be equal to` list[0]
        }
    }

    @Test
    fun `add todo lists`() = runTest {
        val list = todoRepository.todoLists

        viewModel.insertTodoList(todoListName).join()
        viewModel.insertTodoList("list1").join()

        list shouldHaveSize 2
        viewModel.currentList.test {
            awaitItem()
            awaitItem() `should be equal to` list[1]
        }
    }

    @Test
    fun `delete last todo list`() = runTest {
        val list = todoRepository.todoLists
        viewModel.insertTodoList(todoListName).join()
        list shouldHaveSize 1

        viewModel.message.test {
            viewModel.deleteTodoList(list.first()).join()

            awaitItem() `should be instance of` ViewModelMessage.Error::class
        }
        list shouldHaveSize 1
    }

    @Test
    fun `delete one todo list`() = runTest {
        val list = todoRepository.todoLists
        viewModel.insertTodoList(todoListName).join()
        viewModel.insertTodoList("list1").join()
        val item1 = list[0]
        val item2 = list[1]
        list shouldHaveSize 2
        viewModel.currentList.test {
            awaitItem()
            awaitItem() `should be equal to` item2
        }

        viewModel.deleteTodoList(item2).join()

        viewModel.currentList.test {
            awaitItem()
            awaitItem() `should be equal to` item1
        }
        list shouldHaveSize 1
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

    @Test
    fun `add todo`() = runTest {
        viewModel.insertTodoList(todoListName).join()
        viewModel.currentList.test {
            awaitItem()
            awaitItem()

            viewModel.insertTodo(todoName, null).join()
        }

        todoRepository.todos shouldHaveSize 1
    }

    @Test
    fun `delete todo`() = runTest {
        viewModel.insertTodoList(todoListName).join()
        viewModel.currentList.test {
            awaitItem()
            awaitItem()
            viewModel.insertTodo(todoName, null).join()
            todoRepository.todos shouldHaveSize 1
        }

        viewModel.deleteTodo(todoRepository.todos.first()).join()

        todoRepository.todos shouldHaveSize 0
    }

    @Test
    fun `update todo`() = runTest {
        val newName = "newName"
        val newBody = "newBody"
        viewModel.insertTodoList(todoListName).join()
        viewModel.currentList.test {
            awaitItem()
            awaitItem()
            viewModel.insertTodo(todoName, null).join()
            todoRepository.todos shouldHaveSize 1
        }

        viewModel.updateTodo(todoRepository.todos.first(), true).join()
        viewModel.updateTodo(todoRepository.todos.first(), newName, newBody).join()

        todoRepository.todos.first().isCompleted `should be equal to` true
        todoRepository.todos.first().body `should be equal to` newBody
        todoRepository.todos.first().title `should be equal to` newName
    }

    @Test
    fun `get todos`() = runTest {
        viewModel.insertTodoList(todoListName).join()
        viewModel.currentList.test {
            awaitItem()
            awaitItem()
            viewModel.insertTodo(todoName, null).join()
        }
        val todo = todoRepository.todos.first()
        viewModel.insertTodoList("list1").join()
        viewModel.currentList.test {
            awaitItem()
            viewModel.insertTodo("todo1").join()
        }

        viewModel.currentTodos.test {

            awaitItem() `should not contain` todo
        }
    }
}