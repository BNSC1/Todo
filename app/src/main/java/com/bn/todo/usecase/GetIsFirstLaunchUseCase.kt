package com.bn.todo.usecase

import com.bn.todo.data.repository.UserPrefRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetIsFirstLaunchUseCase @Inject constructor(private val repository: UserPrefRepository) {
    operator fun invoke() = repository.getIsFirstTimeLaunch()
}