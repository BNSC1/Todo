package com.bn.todo.ui.viewmodel

import com.bn.todo.arch.BaseViewModel
import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class EntryViewModel @Inject constructor(
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {
    fun getIsNotFirstLaunch() =
        userPrefRepository.getNotFirstTimeLaunch()
}