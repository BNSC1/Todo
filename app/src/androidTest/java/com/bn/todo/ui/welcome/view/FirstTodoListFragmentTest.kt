package com.bn.todo.ui.welcome.view

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.bn.todo.R
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.testutil.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class FirstTodoListFragmentTest {
    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TodoRepository

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)
    }

    @Test
    fun testEmptyNameInput() {
        launchFragmentInHiltContainer<FirstTodoListFragment> {
            navController.setGraph(R.navigation.nav_main)
            navController.setCurrentDestination(R.id.firstTodoListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        pressBack()
        onView(withId(R.id.next_btn)).perform(click())

        runTest {
            repository.queryTodoList().test {
                val res = awaitItem()
                res.size shouldBeEqualTo 1
                res.first().name shouldBeEqualTo context.getString(R.string.default_list_name)
                cancelAndIgnoreRemainingEvents()
            }
        }
        navController.currentDestination?.id shouldBeEqualTo R.id.listFragment
    }
}