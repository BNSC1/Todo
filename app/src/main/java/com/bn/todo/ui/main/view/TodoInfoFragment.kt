package com.bn.todo.ui.main.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bn.todo.R
import com.bn.todo.arch.BaseBottomSheetDialogFragment
import com.bn.todo.arch.CollectsViewModelMessage
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ktx.TAG
import com.bn.todo.ktx.setInvisible
import com.bn.todo.ktx.setVisible
import com.bn.todo.ui.main.viewmodel.TodoOperationViewModel
import com.bn.todo.util.ResUtil
import com.bn.todo.util.TimeUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoInfoFragment : BaseBottomSheetDialogFragment<FragmentTodoInfoBinding>(), CollectsViewModelMessage {
    private val viewModel: TodoOperationViewModel by viewModels()
    private val args by navArgs<TodoInfoFragmentArgs>()
    private val colorAccent: Int by lazy {
        ResUtil.getAttribute(requireContext(), androidx.appcompat.R.attr.colorAccent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickedTodo()
        collectMessage(viewModel)
        with(binding) {
            setupTodo()
            setupCompleteBtn()
            setupDeleteBtn()
            setupEditBtn()
        }
    }

    private fun setClickedTodo() {
        viewModel.setClickedTodo(args.clickedTodo)
    }

    private fun FragmentTodoInfoBinding.setupCompleteBtn() {
        viewModel.clickedTodo.value?.apply {
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
            TodoInfoFragmentDirections.actionEditTodo(
                this@TodoInfoFragment.TAG,
                viewModel.clickedTodo.value
            ).let {
                findNavController().navigate(it)
            }
            dismiss()
        }
    }

    private fun FragmentTodoInfoBinding.setupDeleteBtn(
    ) {
        deleteBtn.setOnClickListener {
            viewModel.clickedTodo.value?.let { todo ->
                viewModel.deleteTodo(todo)
            }
            dismiss()
        }
    }

    private fun FragmentTodoInfoBinding.setupTodo() {
        viewModel.clickedTodo.value?.apply {
            titleText.text = title
            createdTimeText.text = String.format(
                getString(R.string.format_created_time),
                createdTime?.let { TimeUtil.formatToDateTime(it) })
            bodyText.text = body
        }
    }

    private fun setTodoComplete(isCompleted: Boolean) {
        viewModel.clickedTodo.value?.let {
            viewModel.updateTodo(it, isCompleted)
        }
    }

    private fun FragmentTodoInfoBinding.setNotCompletedText() {
        completeText.text = getString(R.string.not_completed)
        completeText.setTextColor(colorAccent)
    }

}