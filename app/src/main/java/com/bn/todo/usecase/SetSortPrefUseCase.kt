package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class SetSortPrefUseCase @Inject constructor(private val repository: UserPrefRepository) {
    suspend operator fun invoke(sortPref: Int) = repository.setSortPref(sortPref)
}