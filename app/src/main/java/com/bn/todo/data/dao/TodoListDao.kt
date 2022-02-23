package com.bn.todo.data.dao

import androidx.room.*
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoListDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(list: TodoList)

    @Query("Select * from `TodoList`")
    fun query(): Flow<List<TodoList>>

    @Query("Select * from `TodoList` where `name` = :queryString")
    fun query(queryString: String): Flow<List<TodoList>>

    @Update
    suspend fun update(list: TodoList)

    @Delete
    suspend fun delete(list: TodoList)
}