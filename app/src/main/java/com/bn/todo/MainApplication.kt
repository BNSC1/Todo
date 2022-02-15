package com.bn.todo

import android.app.Application
import com.bn.todo.di.ApplicationModule
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {
    @Inject
    lateinit var tree: Timber.DebugTree

    override fun onCreate() {
        super.onCreate()
        ApplicationModule.context = applicationContext
        Timber.plant(tree)
    }
}