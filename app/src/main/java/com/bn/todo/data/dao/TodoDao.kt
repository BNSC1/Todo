package com.bn.todo.data.dao

import androidx.room.*
import com.bn.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(todo: Todo)

    @Query("Select * from `Todo` where `listId` = :listId")
    fun query(listId: Int): Flow<List<Todo>>

    @Query("Select * from `Todo` where `title` = :queryString and `listId` = :listId")
    fun query(listId: Int, queryString: String): Flow<List<Todo>>

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}