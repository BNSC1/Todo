package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentTodoListBinding
import javax.inject.Inject

class TodoListFragment : ObserveStateFragment<FragmentTodoListBinding>() {
    @Inject
    override lateinit var viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addTodoBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo().navigate()
            }
        }
    }
}