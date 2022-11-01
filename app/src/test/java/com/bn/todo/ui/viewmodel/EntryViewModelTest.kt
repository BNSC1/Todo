package com.bn.todo.ui.viewmodel

import com.bn.todo.MainCoroutineExtension
import com.bn.todo.data.repository.MockUserPrefRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
class EntryViewModelTest {

    private val repository = MockUserPrefRepository()
    private lateinit var viewModel: EntryViewModel

    @BeforeEach
    fun setup() {
        viewModel = EntryViewModel(repository)
    }

    @Test
    fun `first launch`() = runTest {
        val isFirstLaunch = async { viewModel.isFirstLaunch.first() }
        isFirstLaunch.await() `should be equal to` true
    }

    @Test
    fun `not first launch`() = runTest {
        repository.setIsFirstTimeLaunch(false)
        val isFirstLaunch = async { viewModel.isFirstLaunch.first() }
        isFirstLaunch.await() `should be equal to` false
    }
}