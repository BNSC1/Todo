package com.bn.todo.arch

import androidx.navigation.NavDirections

interface HasNavigation {
    val _activity: NavigationActivity?
    private val navigation get() = _activity?.navigation

    fun NavDirections.navigate() {
        navigation?.navigate(this)
    }
}