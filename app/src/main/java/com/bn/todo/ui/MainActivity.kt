package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.coroutineScope
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.showToast
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val MENU_ORDER = Menu.NONE
@AndroidEntryPoint
class MainActivity : NavigationActivity() {
    @Inject
    lateinit var viewModel: TodoViewModel
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
        with(drawer.navigation.menu) {
            job = lifecycle.coroutineScope.launch {
                viewModel.queryTodoList().collect {
                    if (size != 0) clear()
                    it.forEach { list ->
                        add(R.id.list_group, list.id, MENU_ORDER, list.name).isCheckable = true
                    }
                    add(
                        R.id.list_group,
                        R.id.action_add_list,
                        MENU_ORDER,
                        R.string.action_add_list
                    ).setIcon(R.drawable.ic_add_list)
                    add(
                        R.id.setting_group,
                        R.id.action_settings,
                        MENU_ORDER,
                        R.string.settings
                    ).setIcon(R.drawable.ic_settings)
                    invalidateOptionsMenu()
                }
            }
        }
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

    private fun ActivityMainBinding.onOptionListsSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add_list -> {
            DialogUtil.showInputDialog(
                this@MainActivity,
                "Enter a name for new list",
                inputReceiver = object : DialogUtil.OnInputReceiver {
                    override fun receiveInput(input: String?) {
                        if (!input.isNullOrBlank()) {
                            viewModel.insertTodoList(input).observe(this@MainActivity) {
                                handleState(it, {
                                    //todo: set current list to newly added one.
                                })
                            }
                        }
                    }
                })
            showToast("show add list")
        }
        else -> {
            setSelectedList(item)
        }
    }

    private fun ActivityMainBinding.setSelectedList(item: MenuItem) {
        showToast("item ${item.itemId} selected")
        layoutToolbar.toolbar.title = item.title
        viewModel.shouldRefreshList.postValue(true)
        setCurrentListId(item)
    }

    private fun setCurrentListId(item: MenuItem) {
        job = lifecycle.coroutineScope.launch {
            viewModel.setCurrentListId(item.itemId)
        }
    }

    private fun onOptionSettingsSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun handleState(
        resource: Resource<*>,
        successAction: () -> Unit,
        errorAction: () -> Unit = {},
        loadingAction: () -> Unit = {}
    ) {
        Timber.d("state is ${resource.state}")
        when (resource.state) {
            State.SUCCESS -> successAction()
            State.ERROR -> {
                errorAction()
                resource.message?.let { viewModel.setErrorMsg(it) }
            }
            State.LOADING -> loadingAction()
        }
    }

}