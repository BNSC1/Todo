package com.bn.todo.ui.entry.viewmodel

import app.cash.turbine.test
import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.repository.MockUserPrefRepository
import com.bn.todo.usecase.GetIsFirstLaunchUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
class EntryViewModelTest {

    private val repository = MockUserPrefRepository()
    private val useCase = GetIsFirstLaunchUseCase(repository)
    private lateinit var viewModel: EntryViewModel

    @BeforeEach
    fun setup() {
        viewModel = EntryViewModel(useCase)
    }

    @Test
    fun `first launch`() = runTest {

        viewModel.isFirstLaunch.test {
            awaitItem()

            awaitItem() `should be equal to` true
        }
    }

    @Test
    fun `not first launch`() = runTest {
        repository.setIsFirstTimeLaunch(false)

        viewModel.isFirstLaunch.test {

            awaitItem() `should be equal to` false
        }
    }
}