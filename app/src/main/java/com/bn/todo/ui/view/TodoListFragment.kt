package com.bn.todo.ui.view

import android.annotation.SuppressLint
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
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import timber.log.Timber

@AndroidEntryPoint
class TodoListFragment : ObserveStateFragment<FragmentTodoListBinding>() {
    private var currentList: TodoList? = null

    override val viewModel: TodoViewModel by activityViewModels()
    private lateinit var todosAdapter: TodosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        with(binding) {
            setupAddTodoButton()
            collectTodos()
            setupTodos()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_todo_list, menu)
                initObserveShowCompleted(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_sort -> {
                        viewModel.getSortPref()
                            .collectFirstLifecycleFlow(viewLifecycleOwner) { sortPref ->
                                todosAdapter.apply {
                                    DialogUtil.showRadioDialog(requireContext(),
                                        items = resources.getStringArray(R.array.sort_order_group),
                                        title = getString(R.string.title_sort_by),
                                        defaultIndex = sortPref,
                                        okAction = { index ->
                                            viewModel.setSortPref(index)
                                        })
                                }
                            }
                    }
                    R.id.action_rename_list -> {
                        tryCurrentListAction { list ->
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
                    }
                    R.id.action_delete_list -> {
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
                    R.id.action_clear_completed_todos -> {
                        tryCurrentListAction { list ->
                            DialogUtil.showConfirmDialog(
                                requireContext(),
                                msg = String.format(
                                    getString(R.string.msg_confirm_clear_completed_todos),
                                    list.name
                                ),
                                okAction = {
                                    viewModel.deleteCompletedTodos()
                                        .collectFirstLifecycleFlow(viewLifecycleOwner) { res ->
                                            handleState(res) {
                                                showToast(
                                                    String.format(
                                                        getString(R.string.msg_deleted_todos_format),
                                                        res.data
                                                    )
                                                )
                                            }
                                        }
                                })
                        }

                    }
                    R.id.action_show_completed_todos -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.setShowCompleted(menuItem.isChecked)
                    }
                }
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun collectTodos() {
        viewModel.shouldRefreshList.collectLatestLifecycleFlow(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh) {
                Timber.d("should refresh list")
                currentList = viewModel.getCurrentList()
                (requireActivity() as AppCompatActivity).supportActionBar?.title =
                    currentList?.name
                todosAdapter.replaceItems(viewModel.queryTodo().first())
                viewModel.setShouldRefreshList(false)
            }
        }
    }

    private inline fun tryCurrentListAction(
        nullListAction: () -> Unit = { showToast(getString(R.string.msg_no_list_selected)) },
        action: (TodoList) -> Unit
    ) =
        currentList?.let {
            action(it)
        } ?: nullListAction()

    private fun initObserveShowCompleted(menu: Menu) {
        viewModel.getShowCompleted().collectLatestLifecycleFlow(viewLifecycleOwner) {
            menu.findItem(R.id.action_show_completed_todos).isChecked = it
            menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
        }
    }

}