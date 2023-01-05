package com.bn.todo.usecase

import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.util.TimeUtil
import java.time.OffsetDateTime
import javax.inject.Inject

class InsertTodoUseCase @Inject constructor(private val repository: TodoRepository) {
    suspend operator fun invoke(
        listId: Long,
        title: String,
        body: String?,
        time: OffsetDateTime = TimeUtil.getOffsetDateTime(
            TimeUtil.calendar.toInstant()
        )
    ) = repository.insertTodo(
        title,
        body,
        listId,
        time
    )

}
