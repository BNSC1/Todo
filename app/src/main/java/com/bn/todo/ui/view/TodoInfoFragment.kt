package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.lifecycleScope
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateBottomSheetDialogFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ktx.setInvisible
import com.bn.todo.ktx.setVisible
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
            setupTodo()
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

    private fun FragmentTodoInfoBinding.setupTodo() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            clickedTodo = viewModel.clickedTodo.first()
            titleText.text = clickedTodo.title
            bodyText.text = clickedTodo.body
            if (clickedTodo.isCompleted) {
                completeBtn.setInvisible()
                undoCompleteBtn.setVisible()

                undoCompleteBtn.setOnClickListener {
                    setTodoComplete(false)
                }
            } else {
                completeBtn.setOnClickListener {
                    setTodoComplete(true)
                }
                setNotCompletedText()
            }
        }
    }

    private fun setTodoComplete(isCompleted: Boolean) {
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateTodo(clickedTodo, isCompleted).collect { res ->
                handleState(res, {
                    notifyShouldRefreshList()
                    dismiss()
                })
            }
        }
    }

    private fun FragmentTodoInfoBinding.setNotCompletedText() {
        completeText.text = getString(R.string.not_completed)
        completeText.setTextColor(
            getColor(
                requireContext(),
                R.color.orange_200
            )
        ) //todo: color primary
    }

    private fun notifyShouldRefreshList() {
        viewModel.shouldRefreshList.tryEmit(true)
    }
}