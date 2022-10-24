package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.bn.todo.arch.BaseActivity
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ui.viewmodel.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryActivity : BaseActivity() {
    private val viewModel: EntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isFirstLaunch.collectFirstLifecycleFlow(this) { isFirstLaunch ->
            val intent = Intent(
                this@EntryActivity,
                if (isFirstLaunch) WelcomeActivity::class.java
                else MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}