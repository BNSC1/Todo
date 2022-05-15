package com.bn.todo.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bn.todo.R
import com.bn.todo.arch.ObserveStateFragment
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.FragmentCreateTodoBinding
import com.bn.todo.databinding.LayoutTextInputBinding
import com.bn.todo.ui.MainActivity
import com.bn.todo.ui.viewmodel.TodoViewModel
import com.bn.todo.util.TextInputUtil
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateTodoFragment : ObserveStateFragment<FragmentCreateTodoBinding>() {
    @Inject
    override lateinit var viewModel: TodoViewModel
    private var isAllowed = false
    private val args by navArgs<CreateTodoFragmentArgs>()
    private val sourceFragment: String by lazy { args.sourceFragment }
    private var isEditMode = false
    private lateinit var clickedTodo: Todo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        isEditMode = sourceFragment == TodoInfoFragment.TAG

        with(binding) {
            setupLayout()
            if (isEditMode) {
                (requireActivity() as MainActivity).supportActionBar?.title =
                    getString(R.string.title_edit_todo)
                fetchTodo()
            }
        }
    }

    private fun FragmentCreateTodoBinding.fetchTodo() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            clickedTodo = viewModel.clickedTodo.first()
            layoutTitleInput.input.setText(clickedTodo.title)
            layoutBodyInput.input.setText(clickedTodo.body)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_create_todo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            with(binding) {
                val title = layoutTitleInput.input.text.toString()
                val body = layoutBodyInput.input.text.toString()
                if (isAllowed) {
                    job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        if (isEditMode) {
                            viewModel.updateTodo(clickedTodo, title, body).collect {
                                handleState(it, {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        findNavController().popBackStack()
                                    }
                                })
                            }
                        } else {
                            viewModel.insertTodo(title, body).collect {
                                handleState(it, {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        findNavController().popBackStack()
                                    }
                                })
                            }
                        }
                    }
                } else {
                    layoutTitleInput.setTitleInputError()
                    TextInputUtil.showKeyboard(requireActivity(), layoutTitleInput.input)
                }
            }
        }
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private fun FragmentCreateTodoBinding.setupLayout() {
        layoutTitleInput.apply {
            root.hint = getString(R.string.todo_title)
            root.isErrorEnabled = true
            TextInputUtil.showKeyboard(requireActivity(), input)
            input.addTextChangedListener(object :
                TextInputUtil.TextChangedListener<TextInputEditText>(input) {
                override fun onTextChanged(target: TextInputEditText, s: Editable?) {
                    root.error = ""
                    isAllowed = !target.text.isNullOrBlank()
                }
            })
        }
        layoutBodyInput.apply {
            root.hint = getString(R.string.todo_body)
            input.isSingleLine = false
            input.minLines = 2
        }
    }

    private fun LayoutTextInputBinding.setTitleInputError() {
        root.error = getString(R.string.error_title_required)
    }
}