package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isNotEmpty
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.*
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.main.view.TodoInfoFragment
import com.bn.todo.ui.main.viewmodel.TodoListViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

private const val MENU_ORDER = Menu.NONE

@AndroidEntryPoint
class MainActivity : NavigationActivity(), TodoClickCallback {
    private val viewModel: TodoListViewModel by viewModels()
    private val binding: ActivityMainBinding by viewBinding()
    override val navHostId by lazy { binding.navHost.id }
    private lateinit var toggle: ActionBarDrawerToggle
    private var isRootFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setupToolbar()
            setupDrawer()
            collectTodoLists()
        }
    }

    private fun getIndexById(lists: List<TodoList>, id: Long): Int =
        lists.indexOf(lists.firstOrNull {
            id == it.id
        })

    private fun ActivityMainBinding.collectTodoLists() =
        viewModel.todoLists.combine(viewModel.currentList) { lists, currentList ->
            updateDrawerMenu(lists)
            currentList?.id?.let { getIndexById(lists, it) }
                ?.let {
                    setSelectedListItem(it)
                }
        }.collectLatestLifecycleFlow(this@MainActivity) {}

    private fun openBottomSheet() = TodoInfoFragment().show(supportFragmentManager, "bottom_sheet")

    private fun ActivityMainBinding.setupToolbar() {
        setSupportActionBar(layoutToolbar.toolbar)
        NavigationUI.setupActionBarWithNavController(
            this@MainActivity,
            navigation,
            layoutDrawer
        )
        toggle = ActionBarDrawerToggle(
            this@MainActivity, layoutDrawer,
            layoutToolbar.toolbar, R.string.action_open, R.string.action_close
        )
        toggle.syncState()
        layoutDrawer.addDrawerListener(toggle)
        navigation.addOnDestinationChangedListener { _, destination, _ ->
            isRootFragment = when (destination.id) {
                R.id.listFragment -> true
                else -> false
            }
            updateDrawerNavigation()
        }
    }

    private fun ActivityMainBinding.setupDrawer() {
        drawer.navigation.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
    }

    private fun ActivityMainBinding.updateDrawerMenu(lists: List<TodoList>) {
        with(drawer.navigation.menu) {
            if (isNotEmpty()) clear()
            lists.forEachIndexed { index, list ->
                add(R.id.list_group, index, MENU_ORDER, list.name).isCheckable = true
            }
            add(
                R.id.list_group,
                R.id.action_add_list,
                MENU_ORDER,
                R.string.action_add_list
            ).setIcon(R.drawable.ic_add_list)
//            add(
//                R.id.setting_group,
//                R.id.action_settings,
//                MENU_ORDER,
//                R.string.settings
//            ).setIcon(R.drawable.ic_settings)
            invalidateOptionsMenu()
        }
    }

    private fun ActivityMainBinding.updateDrawerNavigation() {
        supportActionBar?.let { actionBar ->
            if (isRootFragment) {
                actionBar.setDisplayHomeAsUpEnabled(false)
                toggle.syncState()
                layoutToolbar.toolbar.setNavigationOnClickListener {
                    layoutDrawer.openDrawer()
                }
                layoutDrawer.setLockState(false)
            } else {
                layoutDrawer.setLockState(true)
                actionBar.setDisplayHomeAsUpEnabled(true)
                layoutToolbar.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
        }
    }

    private fun ActivityMainBinding.onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            R.id.list_group -> onOptionListsSelected(item)
            R.id.setting_group -> onOptionSettingsSelected(item)
        }

        layoutDrawer.closeDrawer()
        return true
    }

    private fun ActivityMainBinding.onOptionListsSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_add_list -> {
                onActionAddList()
            }
            else -> {
                viewModel.setCurrentList(item.itemId)
                setSelectedListItem(item.itemId)
            }
        }
    }

    private fun onActionAddList() {
        DialogUtil.showInputDialog(
            this@MainActivity,
            getString(R.string.title_input_name_for_list),
            inputReceiver = object : DialogUtil.OnInputReceiver {
                override fun receiveInput(input: String?) {
                    if (!input.isNullOrBlank()) {
                        viewModel.insertTodoList(input)
                    } else {
                        showDialog(message = getString(R.string.title_input_name_for_list))
                    }
                }
            })
    }

    private fun ActivityMainBinding.setSelectedListItem(menuId: Int) {
        if (menuId >= 0 && menuId < drawer.navigation.menu.size()) {
            drawer.navigation.menu.getItem(menuId).isChecked = true
        }
    }

    private fun onOptionSettingsSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    override fun onTodoClick() {
        openBottomSheet()
    }

}