package com.bn.todo.ui

import android.os.Bundle
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity

class WelcomeActivity : NavigationActivity() {

    override val navHostId = R.id.nav_host

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }
}