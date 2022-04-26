package com.bn.todo.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bn.todo.data.dao.TodoDao
import com.bn.todo.data.dao.TodoListDao
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest : TestCase() {
    // get reference to the LanguageDatabase and LanguageDao class
    private lateinit var db: TodoDatabase
    private lateinit var todoListDao: TodoListDao
    private lateinit var todoDao: TodoDao
    private val listId = 1

    // Override function setUp() and annotate it with @Before
    // this function will be called at first when this test class is called
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        todoListDao = db.todoListDao()
        todoDao = db.todoDao()
    }

    // Override function closeDb() and annotate it with @After
    // this function will be called at last when this test class is called
    @After
    fun closeDb() {
        db.close()
    }

    // create a test function and annotate it with @Test
    // here we are first adding an item to the db and then checking if that item
    // is present in the db -- if the item is present then our test cases pass
    @Test
    fun addTodoList() = runBlocking {
        val todoList =
            TodoList("Test").also { Timber.d("insert item id: ${it.id}, name: ${it.name}") }
        todoListDao.insert(todoList)
        val todoLists = todoListDao.query()
        Timber.d("todoLists:")
        todoLists.first().forEach {
            Timber.d("item id: ${it.id}, name: ${it.name}")
        }
        assertEquals(true, todoLists.first().contains(TodoList("Test", listId)))
        todoListDao.update(todoLists.first().first().copy(name = "Test2"))
        val todoLists2 = todoListDao.query()
        Timber.d("todoLists:")
        todoLists2.first().forEach {
            Timber.d("item id: ${it.id}, name: ${it.name}")
        }
        assertEquals(true, todoLists2.first().contains(TodoList("Test2", listId)))
    }

    @Test
    fun modifyTodoList() = runBlocking {

    }

    //    @Test
    fun addTodo() = runBlocking {
        val todo = Todo(
            "Test",
            "test",
            listId
        ).also { Timber.d("insert item id: ${it.id}, name: ${it.title}") }
        todoDao.insert(todo)
        val todos = todoDao.query(listId)
        Timber.d("todoLists:")
        todos.first().forEach {
            Timber.d("item id: ${it.id}, name: ${it.title}")
        }
        assertEquals(true, todos.first().contains(Todo("Test", "test", listId, id = 1)))
    }
}