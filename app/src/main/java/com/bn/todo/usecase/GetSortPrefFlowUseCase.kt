package com.bn.todo.usecase

import com.bn.todo.data.model.TodoSort
import com.bn.todo.data.repository.UserPrefRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSortPrefFlowUseCase @Inject constructor(private val repository: UserPrefRepository) {
    operator fun invoke(default: Int = 0) =
        repository.getSortPref(default).map { pref ->
            TodoSort.values().first { it.ordinal == pref }
        }
}