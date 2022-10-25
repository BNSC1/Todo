package com.bn.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoFilter
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository
) : BaseViewModel() {

    private val _todoLists: StateFlow<List<TodoList>>
    val todoLists get() = _todoLists
    private val _currentList: StateFlow<TodoList?>
    val currentList get() = _currentList

    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo
    private val _listCount = MutableStateFlow(-1)
    val listCount get() = _listCount

    init {
        _todoLists = getTodoListFlow()
        _currentList = getCurrentListFlow()
    }

    fun insertTodoList(name: String) = tryRun {
        todoRepository.insertTodoList(name)
        if (userPrefRepository.getIsFirstTimeLaunch().first()) {
            userPrefRepository.setIsFirstTimeLaunch(false)
        }
    }

    private fun getTodoListFlow(name: String? = null) =
        todoRepository.queryTodoList(name).onEach { _listCount.value = it.size }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun updateTodoList(list: TodoList, name: String) = tryRun {
        todoRepository.updateTodoList(list, name)
    }

    fun deleteTodoList(list: TodoList) = tryRun {
        todoRepository.deleteTodoList(list)
    }

    fun insertTodo(title: String, body: String?) = tryRun {
        todoRepository.insertTodo(title, body, getCurrentListId().first())
    }

    suspend fun queryTodo(name: String? = null) =
        todoRepository.queryTodo(
            TodoFilter(
                getCurrentListId().first(),
                getShowCompleted().first()
            ).apply { this.name = name },
            getSortPref().first()
        )

    fun updateTodo(todo: Todo, name: String, body: String) = tryRun {
        todoRepository.updateTodo(todo, name, body)
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        todoRepository.updateTodo(todo, isCompleted)
    }

    fun deleteTodo(todo: Todo) = tryRun {
        todoRepository.deleteTodo(todo)
    }

    fun deleteCompletedTodos() = tryRun {
        val deletedTodoCount = todoRepository.deleteCompletedTodo(getCurrentListId().first())
        _message.emit(ViewModelMessage.Info.CompletedTodoDeletion(deletedTodoCount))
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    fun getCurrentListId() = userPrefRepository.getCurrentListId(0)

    fun setCurrentListId(id: Int? = null) = tryRun {
        id?.let { userPrefRepository.setCurrentListId(it) } ?: getTodoListFlow().first()
            .firstOrNull()
    }

    fun getShowCompleted(default: Boolean = true) =
        userPrefRepository.getShowCompleted(default)

    fun setShowCompleted(showCompleted: Boolean) = tryRun {
        userPrefRepository.setShowCompleted(showCompleted)
    }

    private fun getCurrentListFlow() = todoLists.combine(getCurrentListId()) { list, id ->
        list.firstOrNull { it.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    fun setSortPref(sortPref: Int) = tryRun {
        userPrefRepository.setSortPref(sortPref)
    }

    fun getSortPref(default: Int = 0) =
        userPrefRepository.getSortPref(default)
}