package com.bn.todo.data.dao

import androidx.room.*
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoSort.ORDER_ADDED
import com.bn.todo.data.model.TodoSort.ORDER_ALPHABET
import com.bn.todo.data.model.TodoSort.ORDER_NOT_COMPLETED
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)


    @Query(
        "Select * from `Todo` where " +
                "case :queryString " +
                "when null then `title` = :queryString and `listId` = :listId " +
                "else `listId` = :listId end " +
                "order by " +
                "case :sortField " +
                "when $ORDER_ADDED then id " +
                "when $ORDER_ALPHABET then title " +
                "when $ORDER_NOT_COMPLETED then isCompleted end asc"
    )
    fun query(
        listId: Int,
        queryString: String? = null,
        sortField: Int = ORDER_ADDED
    ): Flow<List<Todo>>

    @Query("Select * from `Todo` where `listId` = :listId and not `isCompleted`")
    fun queryNotCompleted(listId: Int): Flow<List<Todo>>

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("delete from `Todo` where `listId` = :listId and isCompleted")
    suspend fun deleteCompleted(listId: Int): Int
}