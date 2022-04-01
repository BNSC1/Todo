package com.bn.todo.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ktx.addItemDecoration
import com.bn.todo.ui.callback.TodoClickCallback
import com.bn.todo.ui.view.adapter.TodosAdapter
import com.bn.todo.ui.viewmodel.TodoViewModel
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

        with(binding) {
            addTodoBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo().navigate()
            }
            initObserveTodoList()
            list.addItemDecoration(R.drawable.divider_todo)
            list.adapter = TodosAdapter(todos, object : TodosAdapter.OnItemClickListener {
                override fun onItemClicked(item: Todo) {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted { //todo: survey observeIn
                        (requireActivity() as TodoClickCallback).onTodoClick()
                        viewModel.clickedTodo.emit(item)
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
                    binding.list.adapter!!.notifyDataSetChanged() //todo: duplicate code cleanup
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

    override fun onResume() {
        super.onResume()
        job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.shouldRefreshTitle.emit(true)
        }
    }
}