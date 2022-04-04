package com.bn.todo.ui.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bn.todo.arch.BaseBottomSheetDialogFragment
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TodoInfoFragment : BaseBottomSheetDialogFragment<FragmentTodoInfoBinding>() {
    @Inject
    lateinit var viewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.clickedTodo.collect { todo ->
                    titleText.text = todo.title
                    bodyText.text = todo.body
                }
            }
        }
    }
}