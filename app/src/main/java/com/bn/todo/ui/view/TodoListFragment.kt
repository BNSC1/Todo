package com.bn.todo.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.arch.recyclerview.Clickable
import com.bn.todo.arch.recyclerview.OnItemClickListener
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.collectFirstLifecycleFlow
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
                        (requireActivity() as TodoClickCallback).onTodoClick()
                        viewModel.setClickedTodo(item as Todo)
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
                    currentList?.name
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
                job = viewModel.getSortPref()
                    .collectFirstLifecycleFlow(viewLifecycleOwner) { sortPref ->
                        (binding.list.adapter as TodosAdapter).apply {
                            showRadioDialog(requireContext(),
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
                                            job = viewModel.updateTodoList(it, input)
                                                .collectFirstLifecycleFlow(viewLifecycleOwner) {}
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
                                job = viewModel.deleteTodoList(list)
                                    .collectFirstLifecycleFlow(viewLifecycleOwner) {}
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
                            job = viewModel.deleteCompletedTodos()
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
                item.isChecked = !item.isChecked
                viewModel.setShowCompleted(item.isChecked)
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
        job = viewModel.getShowCompleted().collectLatestLifecycleFlow(viewLifecycleOwner) {
            menu.findItem(R.id.action_show_completed_todos).isChecked = it
            menu.findItem(R.id.action_clear_completed_todos).isEnabled = it
        }
    }

}