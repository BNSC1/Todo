package com.bn.todo.arch

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface HidesActionBar {
    private val Fragment.supportActionBar get() = (activity as AppCompatActivity).supportActionBar

    fun Fragment.hideActionBar() {
        supportActionBar?.hide()
    }

    fun Fragment.showActionBar() {
        supportActionBar?.show()
    }
}