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
import com.bn.todo.ktx.addItemDecoration
import com.bn.todo.ktx.showDialog
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.DialogUtil
import com.bn.todo.util.DialogUtil.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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
            list.addItemDecoration(R.drawable.divider_todo)
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
                if (shouldRefresh) {
                    todos.clear()
                    todos.addAll(viewModel.queryTodo().first())
                    binding.list.adapter!!.notifyDataSetChanged() //todo: optimize
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.queryTodo().collect { data ->
                todos.clear()
                todos.addAll(data)
                binding.list.adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_todo_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_rename_list) {
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
                                            handleState(res, {})
                                        }
                                }
                            } else {
                                showDialog(message = getString(R.string.title_input_name_for_list))
                            }
                        }
                    })
            }
        } else if (item.itemId == R.id.action_delete_list) {
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
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.shouldRefreshTitle.emit(true)
        }
    }
}