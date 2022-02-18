package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bn.todo.util.DataStoreKeys
import com.bn.todo.util.DataStoreMgr

class EntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(
            this,
            if (DataStoreMgr.readPreferences(DataStoreKeys.NOT_FIRST_LAUNCH).equals(true)) {
                MainActivity::class.java
            } else WelcomeActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}