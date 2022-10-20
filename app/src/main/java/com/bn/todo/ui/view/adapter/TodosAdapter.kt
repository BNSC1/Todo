package com.bn.todo.ui.view.adapter

import android.content.Context
import com.bn.todo.R
import com.bn.todo.arch.recyclerview.BaseListAdapter
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.ItemTodoBinding
import com.bn.todo.ktx.setStrikeThrough
import com.bn.todo.ktx.unsetStrikeThrough

class TodosAdapter(
    private val context: Context,
    private val onItemClick: (Todo) -> Unit
) : BaseListAdapter<ItemTodoBinding, Todo>() {

    override val items: List<Todo> = todos

    override val bindAction = { binding: ItemTodoBinding, todo: Todo ->
        val defaultTextColor = context.getColor(R.color.text_highlight)
        fun ItemTodoBinding.unsetAppearanceCompleted() {
            completedCheckbox.isChecked = false
            titleText.unsetStrikeThrough()
            titleText.setTextColor(defaultTextColor)
        }

        fun ItemTodoBinding.setAppearanceCompleted() {
            completedCheckbox.isChecked = true
            titleText.setStrikeThrough()
            titleText.setTextColor(context.getColor(R.color.gray_out))
        }


        with(binding) {
            titleText.text = todo.title
            if (todo.isCompleted) {
                setAppearanceCompleted()
            } else {
                unsetAppearanceCompleted()
            }
            itemLayout.setOnClickListener {
                onItemClick(todo)
            }
        }
    }


}