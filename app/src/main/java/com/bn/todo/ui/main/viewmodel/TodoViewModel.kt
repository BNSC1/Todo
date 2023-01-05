package com.bn.todo.ui.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.bn.todo.R
import com.bn.todo.arch.BaseViewModel
import com.bn.todo.arch.ViewModelMessage
import com.bn.todo.data.model.Todo
import com.bn.todo.data.model.TodoList
import com.bn.todo.data.model.TodoSort
import com.bn.todo.data.repository.TodoRepository
import com.bn.todo.data.repository.UserPrefRepository
import com.bn.todo.usecase.*
import com.bn.todo.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPrefRepository: UserPrefRepository,
    private val getShowCompletedFlowUseCase: GetShowCompletedFlowUseCase,
    private val getSortPrefFlowUseCase: GetSortPrefFlowUseCase,
    private val setSortPrefUseCase: SetSortPrefUseCase,
    private val getTodosFlowUseCase: GetTodosFlowUseCase,
    private val insertTodoListUseCase: InsertTodoListUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    private val setShowCompletedUseCase: SetShowCompletedUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteCompletedTodosUseCase: DeleteCompletedTodosUseCase
) : BaseViewModel() {

    private val _todoLists: StateFlow<List<TodoList>>
    val todoLists get() = _todoLists
    private val _currentList: StateFlow<TodoList?>
    val currentList get() = _currentList
    private val _sortPref: StateFlow<TodoSort>
    val sortPref get() = _sortPref
    private val _currentTodos: StateFlow<List<Todo>>
    val currentTodos get() = _currentTodos
    private val _showCompleted: StateFlow<Boolean>
    val showCompleted get() = _showCompleted
    private val _todoQuery = MutableStateFlow("")
    val todoQuery get() = _todoQuery

    private val _clickedTodo = MutableStateFlow<Todo?>(null)
    val clickedTodo get() = _clickedTodo

    init {
        _todoLists = getTodoListFlow()
        _currentList = getCurrentListFlow()
        _sortPref = getSortPrefFlow()
        _currentTodos = queryTodo()
        _showCompleted = getShowCompletedFlow()
    }

    fun insertTodoList(name: String) = tryRun {
        val insertedTodoListId = insertTodoListUseCase(name)
        setCurrentList(id = insertedTodoListId)
    }

    private fun getTodoListFlow(name: String? = null) =
        todoRepository.queryTodoList(name)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun updateTodoList(list: TodoList, name: String) = tryRun {
        todoRepository.updateTodoList(list, name)
    }

    fun deleteTodoList(list: TodoList) = tryRun {
        val lists = todoLists.value
        if (lists.size > 1) {
            setCurrentList(
                lists[
                        if (lists[0] == list) 1 else 0
                ]
            )
            todoRepository.deleteTodoList(list)
        } else throw IllegalStateException("Attempting to delete last todo list")
    }

    fun insertTodo(title: String, body: String? = null) = tryRun {
        currentList.value?.id?.let { listId ->
            todoRepository.insertTodo(
                title,
                body,
                listId,
                TimeUtil.getOffsetDateTime(TimeUtil.calendar.toInstant())
            )
        }
    }


    private fun queryTodo() =
        getTodosFlowUseCase(todoQuery)
            .catch {
                _message.emit(ViewModelMessage.Error(it.message.toString()))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun updateTodo(todo: Todo, name: String, body: String?) = tryRun {
        updateTodoUseCase(todo, name, body)
    }

    fun updateTodo(todo: Todo, isCompleted: Boolean) = tryRun {
        updateTodoUseCase(todo, isCompleted)
    }

    fun deleteTodo(todo: Todo) = tryRun {
        deleteTodoUseCase(todo)
    }

    fun deleteCompletedTodos() = tryRun {
        val deletedTodoCount = currentList.value?.id?.let {
            deleteCompletedTodosUseCase(it)
        }
        _message.emit(deletedTodoCount?.let {
            ViewModelMessage.Info.CompletedTodoDeletion(it)
        } ?: ViewModelMessage.Error(msgStringId = R.string.error_missing_current_list)
        )
    }

    fun setClickedTodo(todo: Todo) {
        _clickedTodo.value = todo
    }

    fun searchTodo(query: String) {
        _todoQuery.value = query
    }

    private fun getCurrentListIdFlow() = userPrefRepository.getCurrentListId(0)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    private fun setCurrentList(list: TodoList) {
        setCurrentList(list.id)
    }

    private fun setCurrentList(id: Long) = tryRun {
        setCurrentListIdUseCase(id)
    }

    private fun getShowCompletedFlow(default: Boolean = true) =
        getShowCompletedFlowUseCase(default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false
            )

    fun setShowCompleted(showCompleted: Boolean) = tryRun {
        setShowCompletedUseCase(showCompleted)
    }

    private fun getCurrentListFlow() = todoLists.combine(getCurrentListIdFlow()) { list, id ->
        list.firstOrNull { it.id == id } ?: todoLists.value.firstOrNull()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun setSortPref(sortPref: Int) = tryRun {
        setSortPrefUseCase(sortPref)
    }

    private fun getSortPrefFlow(default: Int = 0) =
        getSortPrefFlowUseCase(default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TodoSort.values()[0]
            )
}