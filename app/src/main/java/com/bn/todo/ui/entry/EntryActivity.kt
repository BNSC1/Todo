package com.bn.todo.ui.entry

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.bn.todo.arch.BaseActivity
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ui.MainActivity
import com.bn.todo.ui.entry.viewmodel.EntryViewModel
import com.bn.todo.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryActivity : BaseActivity() {
    private val viewModel: EntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectIsFirstLaunch()
    }

    private fun collectIsFirstLaunch() {
        viewModel.isFirstLaunch.collectLatestLifecycleFlow(this) { isFirstLaunch ->
            onIsFirstLaunchCollected(isFirstLaunch)
        }
    }

    private fun onIsFirstLaunchCollected(isFirstLaunch: Boolean) {
        val intent = Intent(
            this@EntryActivity,
            if (isFirstLaunch) WelcomeActivity::class.java
            else MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}