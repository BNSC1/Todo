package com.bn.todo.ui.welcome

import android.os.Bundle
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : NavigationActivity() {

    override val navHostId = R.id.nav_host

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }
}