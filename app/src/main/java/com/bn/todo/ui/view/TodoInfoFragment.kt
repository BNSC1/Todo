package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateBottomSheetDialogFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ktx.setInvisible
import com.bn.todo.ktx.setVisible
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.ResUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodoInfoFragment : ObserveStateBottomSheetDialogFragment<FragmentTodoInfoBinding>() {
    companion object {
        const val TAG = "TodoInfoFragment"
    }

    @Inject
    override lateinit var viewModel: TodoViewModel
    private lateinit var clickedTodo: Todo
    private val colorAccent: Int by lazy {
        ResUtil.getAttribute(requireContext(), androidx.appcompat.R.attr.colorAccent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupTodo()
            deleteBtn.setOnClickListener {
                viewModel.deleteTodo(clickedTodo)
                dismiss()
            }
            editBtn.setOnClickListener {
                TodoListFragmentDirections.actionCreateTodo(TAG).navigate()
                dismiss()
            }
        }
    }

    private fun FragmentTodoInfoBinding.setupTodo() {
        job = viewModel.clickedTodo.collectFirstLifecycleFlow(viewLifecycleOwner) {
            if (it != null) {
                clickedTodo = it
            }
        }
        titleText.text = clickedTodo.title
        bodyText.text = clickedTodo.body
        if (clickedTodo.isCompleted) {
            completeBtn.setInvisible()
            undoCompleteBtn.setVisible()

            undoCompleteBtn.setOnClickListener {
                setTodoComplete(false)
                dismiss()
            }
        } else {
            completeBtn.setOnClickListener {
                setTodoComplete(true)
                dismiss()
            }
            setNotCompletedText()
        }
    }

    private fun setTodoComplete(isCompleted: Boolean) {
        viewModel.updateTodo(clickedTodo, isCompleted)
    }

    private fun FragmentTodoInfoBinding.setNotCompletedText() {
        completeText.text = getString(R.string.not_completed)
        completeText.setTextColor(colorAccent)
    }

}