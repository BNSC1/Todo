package com.bn.todo.ui.welcome.view

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.bn.todo.R
import com.bn.todo.testutil.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@MediumTest
internal class WelcomeFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun testNextStep() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WelcomeFragment> {
            navController.setGraph(R.navigation.nav_main)
            navController.setCurrentDestination(R.id.welcomeFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.next_btn)).perform(click())

        navController.currentDestination?.id shouldBeEqualTo R.id.firstTodoListFragment
    }
}