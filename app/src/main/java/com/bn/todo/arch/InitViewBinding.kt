package com.bn.todo.arch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

interface InitViewBinding<Binding : ViewBinding> {
    var _binding: Binding?

    @Suppress("UNCHECKED_CAST")
    fun initViewBinding(layoutInflater: LayoutInflater, container: ViewGroup?) {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = method.invoke(null, layoutInflater, container, false) as Binding
    }

    fun removeViewBinding() {
        _binding = null
    }
}