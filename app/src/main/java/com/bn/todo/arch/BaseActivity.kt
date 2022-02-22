package com.bn.todo.arch

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Job

abstract class BaseActivity : AppCompatActivity() {
    protected var job: Job? = null
}