package com.bn.todo.data.dao

import androidx.room.*
import com.bn.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)


    @Query(
        "with completed as (" +
                "select * from `Todo` where case " +
                "when :showCompleted = 0 then isCompleted = 0 " +
                "else 1 end) " +
                "select * from completed where case " +
                "when :queryString != '' " +
                "then (`title` like '%' || :queryString || '%' or `body` like '%' || :queryString || '%') and `listId` = :listId " +
                "else `listId` = :listId end "
    )
    fun query(
        listId: Long,
        queryString: String = "",
        showCompleted: Boolean
    ): Flow<List<Todo>>

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("delete from `Todo` where `listId` = :listId and isCompleted")
    suspend fun deleteCompleted(listId: Long): Int
}