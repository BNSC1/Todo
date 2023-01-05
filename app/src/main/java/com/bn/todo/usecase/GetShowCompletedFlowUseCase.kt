package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class GetShowCompletedFlowUseCase @Inject constructor(private val repository: UserPrefRepository) {
    operator fun invoke(default: Boolean = true) = repository.getShowCompleted(default)
}