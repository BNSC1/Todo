package com.bn.todo.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.arch.recyclerview.Clickable
import com.bn.todo.arch.recyclerview.OnItemClickListener
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import com.bn.todo.util.DialogUtil.showConfirmDialog
import com.bn.todo.util.DialogUtil.showRadioDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TodoListFragment : ObserveStateFragment<FragmentTodoListBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel
    private val todos = ArrayList<Todo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        with(binding) {
            addTodoBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo().navigate()
            }
            initObserveTodoList()
            list.adapter =
                TodosAdapter(requireContext(), todos, object : OnItemClickListener {
                    override fun onItemClick(item: Clickable) {
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            (requireActivity() as TodoClickCallback).onTodoClick()
                            viewModel.clickedTodo.emit(item as Todo)
                        }
                    }
                })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserveTodoList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.shouldRefreshList.collect { shouldRefresh ->
                Timber.d("should refresh list")
                if (shouldRefresh) {
                    todos.clear()
                    todos.addAll(viewModel.queryTodo().first())
                    binding.list.adapter!!.notifyDataSetChanged() //todo: optimize
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_todo_list, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        initObserveShowCompleted(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    (binding.list.adapter as TodosAdapter).apply {
                        showRadioDialog(requireContext(),
                            items = resources.getStringArray(R.array.sort_order_group),
                            title = getString(R.string.title_sort_by),
                            defaultIndex = viewModel.getSortPref().first(),
                            okAction = { index ->
                                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                    viewModel.setSortPref(index)
                                    viewModel.shouldRefreshList.emit(true)
                                }
                            })
                    }
                }
            }
            R.id.action_rename_list -> {
                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    DialogUtil.showInputDialog(
                        requireActivity(),
                        getString(R.string.title_input_name_for_list),
                        defaultValue = viewModel.getCurrentList().name,
                        inputReceiver = object : DialogUtil.OnInputReceiver {
                            override fun receiveInput(input: String?) {
                                if (!input.isNullOrBlank()) {
                                    lifecycleScope.launchWhenStarted {
                                        viewModel.updateTodoList(viewModel.getCurrentList(), input)
                                            .collect { res ->
                                                handleState(res, {
                                                })
                                            }
                                    }
                                } else {
                                    showDialog(message = getString(R.string.title_input_name_for_list))
                                }
                            }
                        })
                }
            }
            R.id.action_delete_list -> {
                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val currentList = viewModel.getCurrentList()
                    showConfirmDialog(requireContext(),
                        msg = String.format(
                            getString(R.string.msg_confirm_delete_list_format),
                            currentList.name
                        ),
                        okAction = {
                            job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                viewModel.deleteTodoList(currentList).collect { res ->
                                    handleState(res, {
                                        viewModel.shouldGoToNewList.value = true
                                    })
                                }
                            }
                        }
                    )
                }
            }
            R.id.action_clear_completed_todos -> {
                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val currentList = viewModel.getCurrentList()
                    showConfirmDialog(requireContext(),
                        msg = String.format(
                            getString(R.string.msg_confirm_clear_completed_todos),
                            currentList.name
                        ),
                        okAction = {
                            job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                viewModel.deleteCompletedTodos().collect { res ->
                                    handleState(res, {
                                        showToast(
                                            String.format(
                                                getString(R.string.msg_deleted_todos_format),
                                                res.data
                                            )
                                        )
                                        viewModel.shouldRefreshList.tryEmit(true)
                                    })
                                }
                            }
                        }
                    )
                }
            }
            R.id.action_show_completed_todos -> {
                item.isChecked = !item.isChecked
                job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.setShowCompleted(item.isChecked)
                    viewModel.shouldRefreshList.emit(true)
                }
            }
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private fun initObserveShowCompleted(menu: Menu) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getShowCompleted().collect {
                menu.findItem(R.id.action_show_completed_todos).isChecked = it
                menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.shouldRefreshTitle.tryEmit(true)
    }
}