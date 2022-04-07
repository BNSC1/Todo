package com.bn.todo.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bn.todo.R
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.ItemTodoBinding
import com.bn.todo.ktx.setStrikeThrough
import com.bn.todo.ktx.unsetStrikeThrough

class TodosAdapter(
    private val context: Context,
    private val todos: List<Todo>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TodosAdapter.ListViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(item: Todo)
    }

    inner class ListViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val defaultTextColor by lazy { binding.titleText.textColors.defaultColor }
        fun bind(todo: Todo) {
            with(binding) {
                titleText.text = todo.title
                if (todo.isCompleted) {
                    setAppearanceCompleted()
                } else {
                    unsetAppearanceCompleted()
                }
                itemLayout.setOnClickListener {
                    onItemClickListener.onItemClicked(todo)
                }
            }
        }

        private fun ItemTodoBinding.setAppearanceCompleted() {
            completedCheckbox.isChecked = true
            titleText.setStrikeThrough()
            titleText.setTextColor(context.getColor(R.color.light_gray))
        }

        private fun ItemTodoBinding.unsetAppearanceCompleted() {
            completedCheckbox.isChecked = false
            titleText.unsetStrikeThrough()
            titleText.setTextColor(defaultTextColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListViewHolder(
        ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.bind(todos[position])

    override fun getItemCount(): Int {
        return todos.size
    }

}