package com.bn.todo.arch.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<Binding : ViewBinding, Item : Listable>(
    private val binding: Binding,
    private val bindAction: (binding: Binding, item: Item) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(item: Item) {
        bindAction(binding, item)
    }
}