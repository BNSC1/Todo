package com.bn.todo.arch

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment

abstract class NavigationActivity : BaseActivity() {
    abstract val navHostId: Int
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
    }

    protected val currentFragment
        get() =
            navHostFragment.childFragmentManager.fragments.getOrNull(0) as? BaseFragment<*>

    val navigation: NavController get() = navHostFragment.navController

    fun NavDirections.navigate() {
        navigation.navigate(this)
    }
}