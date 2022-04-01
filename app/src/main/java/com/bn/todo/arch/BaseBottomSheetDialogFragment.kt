package com.bn.todo.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import java.lang.reflect.ParameterizedType

abstract class BaseBottomSheetDialogFragment<Binding : ViewBinding> : BottomSheetDialogFragment() {
    protected var job: Job? = null
    private val mActivity get() = activity as? NavigationActivity
    private var _binding: Binding? = null
    protected val binding get() = _binding!!

    private val navigation get() = mActivity?.navigation

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

    fun NavDirections.navigate() {
        navigation?.navigate(this)
    }
}