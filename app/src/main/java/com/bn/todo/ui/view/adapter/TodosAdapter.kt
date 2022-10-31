package com.bn.todo.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoSort
import com.bn.todo.databinding.ItemTodoBinding
import com.bn.todo.ui.view.viewholder.TodoViewHolder

class TodosAdapter(
    private val context: Context,
    private val onItemClick: (Todo) -> Unit
) : ListAdapter<Todo, TodoViewHolder>(diffUtil) {
    private val todos = mutableListOf<Todo>()
    private var sortPref: TodoSort = TodoSort.values()[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(layoutInflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(context, todos[position], onItemClick)
    }

    override fun getItemCount() = todos.size

    fun setSort(pref: TodoSort) {
        sortPref = pref
        sort()
    }

    private fun sort() {
        if (todos.isNotEmpty()) {
            when (sortPref) {
                TodoSort.ORDER_ADDED -> todos.sortBy { it.id }
                TodoSort.ORDER_NOT_COMPLETED -> todos.sortBy { !it.isCompleted }
                TodoSort.ORDER_ALPHABET -> todos.sortBy { it.title }
            }
            notifyItemRangeChanged(0, todos.size)
        }
    }

    fun clear() {
        val size = todos.size
        todos.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun replaceItems(todos: List<Todo>) {
        val size = this.todos.size
        this.todos.clear()
        this.todos.addAll(todos)
        sort()
        notifyItemRangeChanged(0, if (size != 0) size else this.todos.size)
    }

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo) =
                oldItem == newItem
        }
    }
}