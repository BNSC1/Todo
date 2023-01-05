package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class GetCurrentListIdFlowUseCase @Inject constructor(private val repository: UserPrefRepository) {
    operator fun invoke(defaultId: Long = 0) = repository.getCurrentListId(defaultId)
}