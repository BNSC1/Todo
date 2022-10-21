package com.bn.todo.ui.view.viewholder

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
            val defaultTextColor = context.getColor(R.color.text_highlight)
            fun ItemTodoBinding.toggleAppearanceCompleted() {
                if (todo.isCompleted) {
                    completedCheckbox.isChecked = true
                    titleText.setStrikeThrough()
                    titleText.setTextColor(context.getColor(R.color.gray_out))
                } else {
                    completedCheckbox.isChecked = false
                    titleText.unsetStrikeThrough()
                    titleText.setTextColor(defaultTextColor)
                }
            }

            titleText.text = todo.title
            toggleAppearanceCompleted()
            itemLayout.setOnClickListener {
                onItemClick(todo)
            }
        }
    }
}