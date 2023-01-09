package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<Binding : ViewBinding> : Fragment(), HasNavigation {
    override val _activity get() = activity as? NavigationActivity
    private var _binding: Binding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewBinding(container)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        removeViewBinding()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun initViewBinding(container: ViewGroup?) {
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

    private fun removeViewBinding() {
        _binding = null
    }

}