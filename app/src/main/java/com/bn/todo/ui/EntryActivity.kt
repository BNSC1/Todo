package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bn.todo.arch.BaseActivity
import com.bn.todo.ui.viewmodel.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EntryActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: EntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = lifecycleScope.launch {
            val intent = Intent(
                this@EntryActivity,
                if (viewModel.getIsNotFirstLaunch().first()) {
                    MainActivity::class.java
                } else
                    WelcomeActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}