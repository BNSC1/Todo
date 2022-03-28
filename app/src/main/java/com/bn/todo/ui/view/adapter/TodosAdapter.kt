package com.bn.todo.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bn.todo.data.model.Todo
import com.bn.todo.databinding.ItemTodoBinding

class TodosAdapter(
    private val todos: List<Todo>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TodosAdapter.ListViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(item: Todo)
    }

    inner class ListViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            with(binding) {
                titleText.text = todo.title
            }
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

    fun update() {

    }
}