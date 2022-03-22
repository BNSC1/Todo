package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentTodoListBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodoListFragment : ObserveStateFragment<FragmentTodoListBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addTodoBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo().navigate()
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