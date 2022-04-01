package com.bn.todo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.NavigationActivity
import com.bn.todo.data.Resource
import com.bn.todo.data.State
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.ActivityMainBinding
import com.bn.todo.ktx.*
import com.bn.todo.ui.view.TodoInfoFragment
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

private const val MENU_ORDER = Menu.NONE
@AndroidEntryPoint
class MainActivity : NavigationActivity() {
    @Inject
    lateinit var viewModel: TodoViewModel
    private val binding: ActivityMainBinding by viewBinding()
    override val navHostId by lazy { binding.navHost.id }
    private var lists = emptyList<TodoList>()
    private var listIndex = 0
    private lateinit var toggle: ActionBarDrawerToggle
    private var isRootFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setupToolbar()
            setupDrawer()
            setupShouldRefreshTitleObserver()
            setupBackdrop()
        }
    }

    private fun setupBackdrop() {
        val fragment = TodoInfoFragment()
        fragment.show(supportFragmentManager, "backdrop")
    }


    private fun ActivityMainBinding.setupShouldRefreshTitleObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.shouldRefreshTitle.collect { shouldRefresh ->
                if (shouldRefresh && lists.isNotEmpty()) {
                    layoutToolbar.toolbar.title = lists[listIndex].name
                    viewModel.shouldRefreshTitle.tryEmit(false)
                }
            }
        }
    }

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

    private fun ActivityMainBinding.setList(itemId: Int? = null) {
        itemId?.let {
            setSelectedListItem(itemId)
        } ?: let {
            job = lifecycleScope.launchWhenStarted {
                setSelectedListItem(viewModel.getCurrentListId().first())
            }
        }
    }

    private fun ActivityMainBinding.setupDrawer() {
        observeTodoList()
        drawer.navigation.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
    }

    private fun ActivityMainBinding.observeTodoList() {
        lifecycleScope.launchWhenStarted {
            viewModel.queryTodoList().collect {
                updateDrawerMenu(it)
            }
        }
    }

    private fun ActivityMainBinding.updateDrawerMenu(lists: List<TodoList>) {
        with(drawer.navigation.menu) {
            if (size != 0) clear()
            this@MainActivity.lists = lists
            this@MainActivity.lists.forEach { list ->
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
            if (viewModel.shouldGoToNewList.value) {
                goToNewList()
            } else setList()
            invalidateOptionsMenu()
        }
    }

    private fun ActivityMainBinding.goToNewList() {
        setList(lists.size)
        viewModel.shouldGoToNewList.value = false
    }

    private fun ActivityMainBinding.updateDrawerNavigation() {
        supportActionBar?.let { actionBar ->
            if (isRootFragment) {
                actionBar.setDisplayHomeAsUpEnabled(false)
                toggle.syncState()
                layoutToolbar.toolbar.setNavigationOnClickListener {
                    layoutDrawer.openDrawer()
                }
                layoutDrawer.setLocked(false)
            } else {
                layoutDrawer.setLocked(true)
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

    private fun ActivityMainBinding.onOptionListsSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add_list -> {
            DialogUtil.showInputDialog(
                this@MainActivity,
                getString(R.string.title_input_name_for_list),
                inputReceiver = object : DialogUtil.OnInputReceiver {
                    override fun receiveInput(input: String?) {
                        if (!input.isNullOrBlank()) {
                            lifecycleScope.launchWhenStarted {
                                viewModel.insertTodoList(input).collect {
                                    handleState(it, {
                                        viewModel.shouldGoToNewList.value = true
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
        listIndex = itemId - 1 //navigation item id starts from 1
        drawer.navigation.menu.getItem(listIndex).isChecked = true
        job = lifecycleScope.launchWhenStarted {
            setCurrentListId(listIndex + 1) //adds it back for next time use
            viewModel.shouldRefreshTitle.emit(true)
            viewModel.shouldRefreshList.emit(true)
        }
    }

    private suspend fun setCurrentListId(listIndex: Int) {
        viewModel.setCurrentListId(listIndex)
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
                resource.message?.let { viewModel.errorMsg.tryEmit(it) }
            }
            State.LOADING -> loadingAction()
        }
    }

}