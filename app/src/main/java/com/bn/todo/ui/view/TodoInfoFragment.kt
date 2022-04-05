package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.arch.ObserveStateBottomSheetDialogFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class TodoInfoFragment : ObserveStateBottomSheetDialogFragment<FragmentTodoInfoBinding>() {
    companion object {
        const val TAG = "TodoInfoFragment"
    }

    @Inject
    override lateinit var viewModel: TodoViewModel
    private lateinit var clickedTodo: Todo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                clickedTodo = viewModel.clickedTodo.first()
                titleText.text = clickedTodo.title
                bodyText.text = clickedTodo.body
            }
            deleteBtn.setOnClickListener {
                job = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    viewModel.deleteTodo(clickedTodo).collect { res ->
                        handleState(res, {
                            dismiss()
                        })
                    }
                }
            }
            editBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo(TAG).navigate()
                dismiss()
            }
        }
    }
}