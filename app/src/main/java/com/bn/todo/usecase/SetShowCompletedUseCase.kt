package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class SetShowCompletedUseCase @Inject constructor(private val repository: UserPrefRepository) {
    suspend operator fun invoke(showCompleted: Boolean) = repository.setShowCompleted(showCompleted)
}
