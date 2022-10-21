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
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.*
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.TodoInfoFragment
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import timber.log.Timber

private const val MENU_ORDER = Menu.NONE

@AndroidEntryPoint
class MainActivity : NavigationActivity(), TodoClickCallback {
    private val viewModel: TodoViewModel by viewModels()
    private val binding: ActivityMainBinding by viewBinding()
    override val navHostId by lazy { binding.navHost.id }
    private lateinit var toggle: ActionBarDrawerToggle
    private var isRootFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setupToolbar()
            setupDrawer()
            collectCurrentListId()
            collectShouldGoToNewList()
        }
    }

    private fun ActivityMainBinding.collectCurrentListId() =
        viewModel.getCurrentListId().collectLatestLifecycleFlow(this@MainActivity) { id ->
            viewModel.queryTodoList().first().let { lists ->
                setSelectedListItem(getIndexById(lists, id))
            }
        }

    private fun getIndexById(lists: List<TodoList>, id: Int): Int =
        lists.indexOf(lists.firstOrNull {
            id == it.id
        })

    private fun ActivityMainBinding.collectShouldGoToNewList() {
        viewModel.shouldGoToNewList.collectLatestLifecycleFlow(this@MainActivity) { shouldGo ->
            if (shouldGo) {
                setList(viewModel.queryTodoList().first().lastIndex)
                viewModel.setShouldGoToNewList(false)
            }
        }
    }

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

    private fun ActivityMainBinding.setList(menuId: Int?) {
        menuId?.let {
            setSelectedListItem(menuId)
        }
    }

    private fun ActivityMainBinding.setupDrawer() {
        collectTodoList()
        drawer.navigation.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
    }

    private fun ActivityMainBinding.collectTodoList() {
        viewModel.queryTodoList().collectLatestLifecycleFlow(this@MainActivity) { lists ->
            updateDrawerMenu(lists)
        }
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
                layoutToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }
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
            else -> {
                setSelectedListItem(item.itemId)
            }
        }
    }

    private fun ActivityMainBinding.setSelectedListItem(menuId: Int) {
        if (menuId >= 0) {
            job = viewModel.queryTodoList().collectFirstLifecycleFlow(this@MainActivity) { lists ->
                viewModel.setCurrentListId(lists[menuId].id)
                drawer.navigation.menu.getItem(menuId).isChecked = true
                viewModel.setShouldRefreshList()
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

    private fun handleState(
        resource: Resource<*>,
        successAction: () -> Unit,
        errorAction: () -> Unit = {},
        loadingAction: () -> Unit = {},
    ) {
        Timber.d("state is ${resource.state}")
        when (resource.state) {
            State.SUCCESS -> successAction()
            State.ERROR -> {
                errorAction()
            }
            State.LOADING -> loadingAction()
        }
    }

    override fun onTodoClick() {
        openBottomSheet()
    }

}