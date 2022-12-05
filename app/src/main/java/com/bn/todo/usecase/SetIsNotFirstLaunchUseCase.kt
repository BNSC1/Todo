package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetIsNotFirstLaunchUseCase @Inject constructor(private val repository: UserPrefRepository) {
    suspend operator fun invoke() = repository.setIsFirstTimeLaunch(false)
}