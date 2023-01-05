package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject

class SetCurrentListIdUseCase @Inject constructor(private val repository: UserPrefRepository) {
    suspend operator fun invoke(id: Long) {
        repository.setCurrentListId(id)
    }
}