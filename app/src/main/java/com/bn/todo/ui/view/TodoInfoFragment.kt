package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.arch.ObserveStateBottomSheetDialogFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ktx.makeInVisible
import com.bn.todo.ktx.makeVisible
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
                completeBtn.makeInVisible()
                undoCompleteBtn.makeVisible()

                undoCompleteBtn.setOnClickListener {
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.updateTodo(clickedTodo, false).collect { res ->
                            handleState(res, {
                                notifyShouldRefreshList()
                                dismiss()
                            })
                        }
                    }
                }
            } else {
                completeBtn.setOnClickListener {
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.updateTodo(clickedTodo, true).collect { res ->
                            handleState(res, {
                                notifyShouldRefreshList()
                                dismiss()
                            })
                        }
                    }
                }
            }
        }
    }

    private fun notifyShouldRefreshList() {
        viewModel.shouldRefreshList.tryEmit(true)
    }
}