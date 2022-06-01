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
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.collectLatestLifecycleFlow
import com.bn.todo.ktx.showDialog
import com.bn.todo.ktx.showToast
import com.bn.todo.ui.MainActivity
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import com.bn.todo.util.DialogUtil.showConfirmDialog
import com.bn.todo.util.DialogUtil.showRadioDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TodoListFragment : ObserveStateFragment<FragmentTodoListBinding>() {
    private var currentList: TodoList? = null

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
            collectTodos()
            list.adapter =
                TodosAdapter(requireContext(), todos, object : OnItemClickListener {
                    override fun onItemClick(item: Clickable) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            (requireActivity() as TodoClickCallback).onTodoClick()
                            viewModel.clickedTodo.emit(item as Todo)
                        }
                    }
                })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectTodos() {
        viewModel.shouldRefreshList.collectLatestLifecycleFlow(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh) {
                Timber.d("should refresh list")
                currentList = viewModel.getCurrentList()
                (requireActivity() as MainActivity).supportActionBar?.title =
                    currentList?.name ?: "Error"
                todos.clear()
                todos.addAll(viewModel.queryTodo().first())
                binding.list.adapter!!.notifyDataSetChanged() //todo: optimize
                viewModel.setShouldRefreshList(false)
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
                job = viewLifecycleOwner.lifecycleScope.launch {
                    (binding.list.adapter as TodosAdapter).apply {
                        showRadioDialog(requireContext(),
                            items = resources.getStringArray(R.array.sort_order_group),
                            title = getString(R.string.title_sort_by),
                            defaultIndex = viewModel.getSortPref().first(),
                            okAction = { index ->
                                job = viewLifecycleOwner.lifecycleScope.launch {
                                    viewModel.setSortPref(index)
                                }
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
                                    job = viewLifecycleOwner.lifecycleScope.launch {
                                        currentList?.let {
                                            viewModel.updateTodoList(it, input)
                                                .collect { res ->
                                                    handleState(res, {})
                                                }
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
                if (viewModel.listCount.value > 1) {
                    tryCurrentListAction { list ->
                        showConfirmDialog(
                            requireContext(),
                            msg = String.format(
                                getString(R.string.msg_confirm_delete_list_format),
                                list.name
                            ),
                            okAction = {
                                job = viewLifecycleOwner.lifecycleScope.launch {
                                    viewModel.deleteTodoList(list)
                                        .collect { res ->
                                            handleState(
                                                res,
                                                {})
                                        }
                                }
                            }
                        )
                    }
                } else {
                    showDialog(messageStringId = R.string.msg_cannot_delete_last_list)
                }
            }
            R.id.action_clear_completed_todos -> {
                tryCurrentListAction { list ->
                    showConfirmDialog(
                        requireContext(),
                        msg = String.format(
                            getString(R.string.msg_confirm_clear_completed_todos),
                            list.name
                        ),
                        okAction = {
                            job = viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.deleteCompletedTodos().collect { res ->
                                    handleState(res, {
                                        showToast(
                                            String.format(
                                                getString(R.string.msg_deleted_todos_format),
                                                res.data
                                            )
                                        )
                                    })
                                }
                            }
                        })
                        }

            }
            R.id.action_show_completed_todos -> {
                item.isChecked = !item.isChecked
                job = viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setShowCompleted(item.isChecked)
                }
            }
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private inline fun tryCurrentListAction(
        nullListAction: () -> Unit = { showToast(getString(R.string.msg_no_list_selected)) },
        action: (TodoList) -> Unit
    ) =
        currentList?.let {
            action(it)
        } ?: nullListAction()

    private fun initObserveShowCompleted(menu: Menu) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getShowCompleted().collect {
                menu.findItem(R.id.action_show_completed_todos).isChecked = it
                menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
            }
        }
    }

}