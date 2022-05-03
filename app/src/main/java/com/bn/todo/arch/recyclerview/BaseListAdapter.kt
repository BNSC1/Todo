package com.bn.todo.arch.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseListAdapter<Binding : ViewBinding, Item : Any> :
    RecyclerView.Adapter<BaseViewHolder<Binding, Item>>(), ListUpdateCallback {
    protected abstract val items: List<Item>
    protected abstract val bindAction: (binding: Binding, item: Item) -> Unit

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Binding, Item> {
        return BaseViewHolder(
            initViewBinding(parent), bindAction
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Binding, Item>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding(parent: ViewGroup): Binding {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, LayoutInflater.from(parent.context), parent, false) as Binding
    }

    override fun onInserted(position: Int, count: Int) {
        notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        notifyItemRangeRemoved(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        notifyItemRangeChanged(position, count, payload)
    }
}