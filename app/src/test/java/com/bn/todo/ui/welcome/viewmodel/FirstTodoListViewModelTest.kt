package com.bn.todo.ui.welcome.viewmodel

import app.cash.turbine.test
import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.repository.MockTodoRepository
import com.bn.todo.data.repository.MockUserPrefRepository
import com.bn.todo.usecase.InsertTodoListUseCase
import com.bn.todo.usecase.SetIsNotFirstLaunchUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class FirstTodoListViewModelTest {
    private lateinit var viewModel: FirstTodoListViewModel
    private lateinit var insertTodoListUseCase: InsertTodoListUseCase
    private lateinit var setIsNotFirstLaunchUseCase: SetIsNotFirstLaunchUseCase
    private val todoRepository = MockTodoRepository()
    private val userPrefRepository = MockUserPrefRepository()
    private val todoListName = "todoListName"

    @BeforeEach
    fun setup() {
        insertTodoListUseCase = InsertTodoListUseCase(todoRepository)
        setIsNotFirstLaunchUseCase = SetIsNotFirstLaunchUseCase(userPrefRepository)
        viewModel = FirstTodoListViewModel(insertTodoListUseCase, setIsNotFirstLaunchUseCase)
    }

    @Test
    fun `add first todo list`() = runTest {
        viewModel.insertTodoList(todoListName).join()

        todoRepository.todoLists shouldHaveSize 1
        userPrefRepository.getIsFirstTimeLaunch().test {
            awaitItem() `should be equal to` false
            cancelAndIgnoreRemainingEvents()
        }
    }
}