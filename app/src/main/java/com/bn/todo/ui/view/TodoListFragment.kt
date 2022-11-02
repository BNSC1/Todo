package com.bn.todo.ui.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.BaseFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.model.TodoSort
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class TodoListFragment : BaseFragment<FragmentTodoListBinding>(), CollectsViewModelMessage {
    private var currentList: TodoList? = null
    private var sortPref = TodoSort.values()[0]

    override val viewModel: TodoViewModel by activityViewModels()
    private lateinit var todosAdapter: TodosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectMessage()
        setupMenu()

        with(binding) {
            setupAddTodoButton()
            setupTodos()
            collectCurrentList()
            collectTodos()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_todo_list, menu)
                collectShowCompleted(menu)
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
                    viewModel.deleteCompletedTodos()
                })
        }
    }

    private fun onActionDelete() {
        if (viewModel.listCount.value > 1) {
            tryCurrentListAction { list ->
                DialogUtil.showConfirmDialog(
                    requireContext(),
                    msg = String.format(
                        getString(R.string.msg_confirm_delete_list_format),
                        list.name
                    ),
                    okAction = {
                        viewModel.deleteTodoList(list)
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
                        currentList?.let {
                            viewModel.updateTodoList(it, input)
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
            defaultIndex = sortPref.ordinal,
            okAction = { index ->
                viewModel.setSortPref(index)
            })
    }

    private fun FragmentTodoListBinding.setupTodos() {
        todosAdapter = TodosAdapter(requireContext()) {
            (requireActivity() as TodoClickCallback).onTodoClick()
            viewModel.setClickedTodo(it)
        }
        list.adapter = todosAdapter
    }

    private fun FragmentTodoListBinding.setupAddTodoButton() {
        addTodoBtn.setOnClickListener {
            TodoListFragmentDirections.actionCreateTodo().navigate()
        }
    }

    private fun collectCurrentList() {
        viewModel.currentList.collectLatestLifecycleFlow(viewLifecycleOwner) { list ->
            currentList = list
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                currentList?.name
        }
    }

    private fun collectTodos() {
        viewModel.currentTodos.combine(viewModel.sortPref) { todos, pref ->
            sortPref = pref
            todos?.apply {
                todosAdapter.submitList(
                    when (sortPref) {
                        TodoSort.ORDER_ADDED -> sortedBy { it.id }
                        TodoSort.ORDER_NOT_COMPLETED -> sortedBy { it.isCompleted }
                        TodoSort.ORDER_ALPHABET -> sortedBy { it.title }
                    }
                )
            }
        }.collectLatestLifecycleFlow(viewLifecycleOwner) {}
    }

    private inline fun tryCurrentListAction(
        nullListAction: () -> Unit = { showToast(getString(R.string.msg_no_list_selected)) },
        action: (TodoList) -> Unit
    ) =
        currentList?.let {
            action(it)
        } ?: nullListAction()

    private fun collectShowCompleted(menu: Menu) {
        viewModel.showCompleted.collectLatestLifecycleFlow(viewLifecycleOwner) {
            menu.findItem(R.id.action_show_completed_todos).isChecked = it
            menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
        }
    }
}