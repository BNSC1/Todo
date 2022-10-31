package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateBottomSheetDialogFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ktx.TAG
import com.bn.todo.ktx.collectFirstLifecycleFlow
import com.bn.todo.ktx.setInvisible
import com.bn.todo.ktx.setVisible
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.ResUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoInfoFragment : ObserveStateBottomSheetDialogFragment<FragmentTodoInfoBinding>() {
    override val viewModel: TodoViewModel by activityViewModels()
    private var clickedTodo: Todo? = null
    private val colorAccent: Int by lazy {
        ResUtil.getAttribute(requireContext(), androidx.appcompat.R.attr.colorAccent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupTodo()
            setupCompleteBtn()
            setupDeleteBtn()
            setupEditBtn()
        }
    }

    private fun FragmentTodoInfoBinding.setupCompleteBtn() {
        clickedTodo?.apply {
            if (isCompleted) {
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
    }

    private fun FragmentTodoInfoBinding.setupEditBtn() {
        editBtn.setOnClickListener {
            TodoListFragmentDirections.actionCreateTodo(this@TodoInfoFragment.TAG).navigate()
            dismiss()
        }
    }

    private fun FragmentTodoInfoBinding.setupDeleteBtn(
    ) {
        deleteBtn.setOnClickListener {
            clickedTodo?.let { todo -> viewModel.deleteTodo(todo) }
            dismiss()
        }
    }

    private fun FragmentTodoInfoBinding.setupTodo() {
        viewModel.clickedTodo.collectFirstLifecycleFlow(viewLifecycleOwner) {
            clickedTodo = it
        }

        clickedTodo?.apply {
            titleText.text = title
            bodyText.text = body
        }
    }

    private fun setTodoComplete(isCompleted: Boolean) {
        clickedTodo?.let { viewModel.updateTodo(it, isCompleted) }
    }

    private fun FragmentTodoInfoBinding.setNotCompletedText() {
        completeText.text = getString(R.string.not_completed)
        completeText.setTextColor(colorAccent)
    }

}