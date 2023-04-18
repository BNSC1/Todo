package com.bn.todo.arch

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

abstract class NavigationActivity : AppCompatActivity() {
    abstract val navHostId: Int
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
    }

    val navigation: NavController get() = navHostFragment.navController
}