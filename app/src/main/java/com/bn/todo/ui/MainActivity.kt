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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.showDialog
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
    private var lists: List<TodoList> = emptyList()
    private var currentItemId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            setupDrawer()
        }
    }

    private suspend fun ActivityMainBinding.setCurrentList() {
        viewModel.loadCurrentListId().collect {
            setSelectedListItem(it)
        }
    }

    private fun ActivityMainBinding.setupDrawer() {
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
        NavigationUI.setupActionBarWithNavController(
            this@MainActivity,
            navigation,
            layoutDrawer
        )
        layoutToolbar.toolbar.setNavigationOnClickListener {
            layoutDrawer.openDrawer(GravityCompat.START)
        }
        drawer.navigation.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
        addDrawerMenu()
    }

    private fun ActivityMainBinding.addDrawerMenu() {
        with(drawer.navigation.menu) {
            job = lifecycleScope.launch {
                viewModel.queryTodoList().collect {
                    if (size != 0) clear()
                    lists = it
                    lists.forEach { list ->
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
                    setCurrentList()
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
                getString(R.string.title_input_name_for_list),
                inputReceiver = object : DialogUtil.OnInputReceiver {
                    override fun receiveInput(input: String?) {
                        if (!input.isNullOrBlank()) {
                            job = lifecycleScope.launch {
                                viewModel.insertTodoList(input).collect {
                                    handleState(it, {
                                        viewModel.shouldGoToNewList.postValue(true)
                                    })
                                }
                            }
                        } else {
                            showDialog(message = getString(R.string.title_input_name_for_list))
                        }
                    }
                })
            showToast("show add list, ${item.itemId}")
        }
        else -> {
            setSelectedListItem(item.itemId)
        }
    }

    private fun ActivityMainBinding.setSelectedListItem(itemId: Int) {
        if (currentItemId != itemId) {
            Timber.d("setSelectedListItem")
            val listIndex = itemId - 1 //navigation item id starts from 1
            drawer.navigation.menu.getItem(listIndex).isChecked = true
            layoutToolbar.toolbar.title = lists[listIndex].name
            viewModel.shouldRefreshList.postValue(true)
            saveCurrentListId(listIndex + 1) //adds it back for next time use
            currentItemId = itemId
        }
    }

    private fun saveCurrentListId(listIndex: Int) {
        job = lifecycleScope.launch {
            viewModel.saveCurrentListId(listIndex)
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