package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import com.bn.todo.arch.BaseActivity
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ui.viewmodel.EntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: EntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getIsNotFirstLaunch().collectFirstLifecycleFlow(this) { isNotFirstLaunch ->
            val intent = Intent(
                this@EntryActivity,
                if (isNotFirstLaunch) MainActivity::class.java
                else WelcomeActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}