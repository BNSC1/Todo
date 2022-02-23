package com.bn.todo.arch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel() {
    protected var job: Job? = null
    val errorMsg: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    fun setErrorMsg(message: String) = errorMsg.postValue(message)
}
