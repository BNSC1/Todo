package com.bn.todo.ui.main.view.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bn.todo.R
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.ItemTodoBinding
import com.bn.todo.ktx.setStrikeThrough
import com.bn.todo.ktx.unsetStrikeThrough

class TodoViewHolder(private val binding: ItemTodoBinding) : ViewHolder(binding.root) {

    fun bind(context: Context, todo: Todo, onItemClick: (Todo) -> Unit) {
        with(binding) {
            titleText.text = todo.title
            setAppearanceCompleted(
                todo.isCompleted,
                defaultTextColor = context.getColor(R.color.text_highlight),
                completedTextColor = context.getColor(R.color.gray_out)
            )
            itemLayout.setOnClickListener {
                onItemClick(todo)
            }
        }
    }

    private fun ItemTodoBinding.setAppearanceCompleted(
        isCompleted: Boolean,
        defaultTextColor: Int,
        completedTextColor: Int
    ) {
        if (isCompleted) {
            completedCheckbox.isChecked = true
            titleText.setStrikeThrough()
            titleText.setTextColor(completedTextColor)
        } else {
            completedCheckbox.isChecked = false
            titleText.unsetStrikeThrough()
            titleText.setTextColor(defaultTextColor)
        }
    }
}