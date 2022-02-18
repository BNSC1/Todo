package com.bn.todo.ui

import android.os.Bundle
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity

class MainActivity : NavigationActivity() {

    override val navHostId = R.id.main_nav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}