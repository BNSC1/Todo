package com.bn.todo.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.R.id.search_src_text
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.*
import com.bn.todo.ui.main.view.adapter.TodosAdapter
import com.bn.todo.ui.main.viewmodel.TodoListViewModel
import com.bn.todo.ui.main.viewmodel.TodoOperationViewModel
import com.bn.todo.ui.main.viewmodel.TodoShowViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoListFragment : BaseFragment<FragmentTodoListBinding>(), CollectsViewModelMessage {
    override val viewModel: TodoShowViewModel by viewModels()
    private val todoOperationViewModel: TodoOperationViewModel by viewModels()
    private val listViewModel: TodoListViewModel by viewModels()
    private lateinit var todosAdapter: TodosAdapter
    private lateinit var searchView: SearchView
    private lateinit var onBackPressedCancelSearchCallback: OnBackPressedCallback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnBackPressedCallback()
        collectMessage()
        setupMenu()

        with(binding) {
            setupAddTodoButton()
            setupTodos()
        }
        collectCurrentList()
        collectTodos()
    }

    private fun setupOnBackPressedCallback() {
        onBackPressedCancelSearchCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                searchView.isIconified = true
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCancelSearchCallback
        )
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_todo_list, menu)
                searchView = menu.findItem(R.id.action_search).actionView as SearchView
                setupQuery(searchView)
                collectShowCompleted(menu)
                collectQuery()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_sort -> {
                        onActionSort()
                    }
                    R.id.action_rename_list -> {
                        tryCurrentListAction { list ->
                            onActionRename(list)
                        }
                    }
                    R.id.action_delete_list -> {
                        onActionDelete()
                    }
                    R.id.action_clear_completed_todos -> {
                        onActionClearCompleted()

                    }
                    R.id.action_show_completed_todos -> {
                        onActionShowCompleted(menuItem)
                    }
                }
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun collectQuery() {
        viewModel.todoQuery.collectLatestLifecycleFlow(viewLifecycleOwner) {
            handleQueryNotice(it)
        }
    }

    private fun handleQueryNotice(query: String) =
        binding.queryNoticeText.apply {
            if (query.isNotEmpty()) {
                text = String.format(getString(R.string.format_query_notice), query)
                setVisible()
            } else {
                setGone()
            }
        }

    private fun setupQuery(searchView: SearchView) {
        searchView.apply {
            findViewById<EditText>(search_src_text).apply {
                hint = getString(R.string.action_search)
            }
            setOnCloseListener {
                viewModel.searchTodo("")
                onBackPressedCancelSearchCallback.isEnabled = false
                false
            }
            setOnSearchClickListener {
                (it as SearchView).setQuery(
                    viewModel.todoQuery.value,
                    false
                )
                onBackPressedCancelSearchCallback.isEnabled = true
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchTodo(newText ?: "")
                    return false
                }

                override fun onQueryTextSubmit(query: String?) = false
            })
        }
    }

    private fun onActionShowCompleted(menuItem: MenuItem) {
        menuItem.isChecked = !menuItem.isChecked
        viewModel.setShowCompleted(menuItem.isChecked)
    }

    private fun onActionClearCompleted() {
        tryCurrentListAction { list ->
            DialogUtil.showConfirmDialog(
                requireContext(),
                msg = String.format(
                    getString(R.string.msg_confirm_clear_completed_todos),
                    list.name
                ),
                okAction = {
                    todoOperationViewModel.deleteCompletedTodos(listViewModel.currentList.value?.id)
                })
        }
    }

    private fun onActionDelete() {
        if (listViewModel.todoLists.value.size > 1) {
            tryCurrentListAction { list ->
                DialogUtil.showConfirmDialog(
                    requireContext(),
                    msg = String.format(
                        getString(R.string.msg_confirm_delete_list_format),
                        list.name
                    ),
                    okAction = {
                        listViewModel.deleteTodoList(list)
                    }
                )
            }
        } else {
            showDialog(messageStringId = R.string.msg_cannot_delete_last_list)
        }
    }

    private fun onActionRename(list: TodoList) {
        DialogUtil.showInputDialog(
            requireActivity(),
            getString(R.string.title_input_name_for_list),
            defaultValue = list.name,
            inputReceiver = object : DialogUtil.OnInputReceiver {
                override fun receiveInput(input: String?) {
                    if (!input.isNullOrBlank()) {
                        listViewModel.currentList.value?.let {
                            listViewModel.updateTodoList(it, input)
                        }
                    } else {
                        showDialog(message = getString(R.string.title_input_name_for_list))
                    }
                }
            })
    }

    private fun onActionSort() {
        DialogUtil.showRadioDialog(requireContext(),
            items = resources.getStringArray(R.array.sort_order_group),
            title = getString(R.string.title_sort_by),
            defaultIndex = viewModel.sortPref.value.ordinal,
            okAction = { index ->
                viewModel.setSortPref(index)
            })
    }

    private fun FragmentTodoListBinding.setupTodos() {
        todosAdapter = TodosAdapter(requireContext()) {
            TodoListFragmentDirections.actionToTodoInfoFragment(it).navigate()
        }
        list.adapter = todosAdapter
    }

    private fun FragmentTodoListBinding.setupAddTodoButton() {
        addTodoBtn.setOnClickListener {
            TodoListFragmentDirections.actionCreateTodo().navigate()
        }
    }

    private fun collectCurrentList() {
        listViewModel.currentList.collectLatestLifecycleFlow(viewLifecycleOwner) { list ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                list?.name
        }
    }

    private fun collectTodos() {
        viewModel.currentTodos.collectLatestLifecycleFlow(viewLifecycleOwner) { todos ->
            todosAdapter.submitList(todos)
            handleNoResultView(todos)
        }
    }

    private fun handleNoResultView(todos: List<Todo>) {
        binding.noResultText.apply {
            if (todos.isEmpty()) {
                setVisible()
            } else setInvisible()
        }
    }

    private inline fun tryCurrentListAction(
        nullListAction: () -> Unit = { showToast(getString(R.string.msg_no_list_selected)) },
        action: (TodoList) -> Unit
    ) =
        listViewModel.currentList.value?.let {
            action(it)
        } ?: nullListAction()

    private fun collectShowCompleted(menu: Menu) {
        viewModel.showCompleted.collectLatestLifecycleFlow(viewLifecycleOwner) {
            menu.findItem(R.id.action_show_completed_todos).isChecked = it
            menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
        }
    }
}