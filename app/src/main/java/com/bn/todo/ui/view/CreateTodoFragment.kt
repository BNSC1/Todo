package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.databinding.FragmentCreateTodoBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTodoFragment : ObserveStateFragment<FragmentCreateTodoBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            setupLayout()

        }
    }

    private fun FragmentCreateTodoBinding.setupLayout() {
        layoutTitleInput.root.hint = getString(R.string.hint_input_todo_title)
        layoutBodyInput.apply {
            root.hint = getString(R.string.hint_input_todo_body)
            input.isSingleLine = false
            input.minLines = 2
        }
    }
}