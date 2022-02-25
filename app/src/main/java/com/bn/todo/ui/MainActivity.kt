package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.showToast

class MainActivity : NavigationActivity() {
    private lateinit var binding: ActivityMainBinding
    override val navHostId = R.id.nav_host

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            setupDrawer()
        }
    }

    private fun ActivityMainBinding.setupDrawer() {
        addDrawerMenu()
        setSupportActionBar(layoutToolbar.toolbar)
        val toggle = ActionBarDrawerToggle(
            this@MainActivity, layoutDrawer,
            layoutToolbar.toolbar, R.string.action_open, R.string.action_close
        )
        toggle.syncState()
        layoutDrawer.addDrawerListener(toggle)
        layoutDrawer.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }
        })
        drawer.navigation.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
        NavigationUI.setupActionBarWithNavController(
            this@MainActivity,
            navigation,
            layoutDrawer
        )
        layoutToolbar.toolbar.setNavigationOnClickListener {
            layoutDrawer.openDrawer(GravityCompat.START)
        }
    }

    private fun ActivityMainBinding.addDrawerMenu() {
        drawer.navigation.menu
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return true
    }

    override fun onSupportNavigateUp() =
        NavigationUI.navigateUp(navigation, binding.layoutDrawer)

    private fun ActivityMainBinding.onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            R.id.list_group -> onOptionListsSelected(item)
            R.id.setting_group -> onOptionSettingsSelected(item)
        }

        layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onOptionListsSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_add_list -> {
                //todo: add list function
                showToast("show add list")
            }
        }
    }

    private fun onOptionSettingsSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }
}