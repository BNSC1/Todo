package com.bn.todo.ui.view

import com.bn.todo.arch.BaseBottomSheetDialogFragment
import com.bn.todo.databinding.FragmentTodoInfoBinding
import com.bn.todo.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodoInfoFragment : BaseBottomSheetDialogFragment<FragmentTodoInfoBinding>() {
    @Inject
    lateinit var viewModel: TodoViewModel

}