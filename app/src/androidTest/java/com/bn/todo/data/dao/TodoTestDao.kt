package com.bn.todo.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bn.todo.testutil.InstantTaskExecutorExtension
import com.bn.todo.data.db.TodoDatabase
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
internal class TodoTestDao {
    private lateinit var todoListDao: TodoListDao
    private lateinit var todoDao: TodoDao
    private lateinit var todoDatabase: TodoDatabase
    private val listName = "listName"
    private val listId = 1L
    private val todoTitlePartial = "todo"
    private val todoTitle = todoTitlePartial + "Title"

    @BeforeEach
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        todoDatabase = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        todoListDao = todoDatabase.todoListDao()
        todoDao = todoDatabase.todoDao()
        runBlocking {
            todoListDao.insert(TodoList(listName, listId))
        }
    }

    @AfterEach
    fun closeDB() {
        todoDatabase.close()
    }

    @Test
    fun insertTodo() = runTest {
        val todo = Todo(
            todoTitle,
            id = 1,
            listId = listId,
            body = null
        )

        todoDao.insert(todo)

        todoDao.query(listId).first() shouldContain todo
    }

    @Test
    fun deleteTodo() = runTest {
        val todo = Todo(
            todoTitle,
            id = 1,
            listId = listId,
            body = null
        )
        todoDao.insert(todo)
        todoDao.query(listId).first() shouldContain todo

        todoDao.delete(todo)

        todoDao.query(listId).first() shouldHaveSize 0
    }

    @Test
    fun updateTodo() = runTest {
        val todo = Todo(
            todoTitle,
            id = 1,
            listId = listId,
            body = null
        )
        todoDao.insert(todo)
        todoDao.query(listId).first() shouldContain todo
        val newTodo = todo.copy("new")

        todoDao.update(newTodo)

        todoDao.query(listId).first().let {
            it shouldHaveSize 1
            it shouldContain newTodo
        }
    }

    @Test
    fun queryTodosByListId() = runTest {
        val anotherList = TodoList("another list", 2)
        todoListDao.insert(anotherList)
        val todo1 = Todo(todoTitle, id = 1, listId = listId, body = null)
        val todo2 = Todo(todoTitle, id = 2, listId = listId, body = null)
        val todo3 = Todo(todoTitle, id = 3, listId = 2, body = null)
        listOf(todo1, todo2, todo3).forEach {
            todoDao.insert(it)
        }

        val res = todoDao.query(listId).first()

        res.let {
            it shouldHaveSize 2
            it shouldContainAll arrayOf(todo1, todo2)
        }
    }

    @Test
    fun queryTodosByListIdAndTitle() = runTest {
        val anotherList = TodoList("another list", 2)
        todoListDao.insert(anotherList)
        val todo1 = Todo(todoTitle, id = 1, listId = listId, body = null)
        val todo2 = Todo("title1", id = 2, listId = listId, body = null)
        val todo3 = Todo(todoTitle, id = 3, listId = 2, body = null)
        listOf(todo1, todo2, todo3).forEach {
            todoDao.insert(it)
        }

        val res = todoDao.query(listId, todoTitle).first()

        res.let {
            it shouldNotContain todo2
            it shouldNotContain todo3
            it shouldContain todo1
        }
    }
    
    @Test
    fun queryTodosByListIdAndPartialTitle() = runTest {
        val anotherList = TodoList("another list", 2)
        todoListDao.insert(anotherList)
        val todo1 = Todo(todoTitle, id = 1, listId = listId, body = null)
        val todo2 = Todo("title1", id = 2, listId = listId, body = null)
        val todo3 = Todo(todoTitle, id = 3, listId = 2, body = null)
        listOf(todo1, todo2, todo3).forEach {
            todoDao.insert(it)
        }

        val res = todoDao.query(listId, todoTitlePartial).first()

        res.let {
            it shouldNotContain todo2
            it shouldNotContain todo3
            it shouldContain todo1
        }
    }
}