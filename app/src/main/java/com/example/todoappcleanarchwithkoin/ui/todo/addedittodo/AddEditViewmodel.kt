package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.source.local.model.group.GroupTodoEntity
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.use_case.TodoUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get
import java.util.*
import kotlin.Exception

class AddEditViewModel(
    private val todoUseCase: TodoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val coreTodoEntity = savedStateHandle.get<TodoEntity>("todoEntity")

    var todoIsCompleted: Boolean = false
    var todoId: Long = -1
    var todoGroupName: String = "Default"
    var todoDescription: String = ""
    var todoDateAndTime: Date? = null
    var todoTitle: String = ""
    val listGroup = todoUseCase.getAllGroupTodoEntity()
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (coreTodoEntity!!.id != -1L) {
            todoId = coreTodoEntity.id
            todoIsCompleted = coreTodoEntity.isCompleted
            todoGroupName = coreTodoEntity.groupName
            todoDescription = coreTodoEntity.description
            todoDateAndTime = coreTodoEntity.dateAndTime
            todoTitle = coreTodoEntity.title
        }
    }

    private fun getCurrentTodo(): TodoEntity {
        return TodoEntity(
            id = todoId,
            title = todoTitle,
            description = todoDescription,
            dateAndTime = todoDateAndTime,
            isCompleted = todoIsCompleted,
            isExpired = coreTodoEntity?.isExpired ?: false,
            groupName = todoGroupName
        )
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.EnteredTitle -> {
                todoTitle = event.title
            }

            is AddEditEvent.EnteredDescription -> {
                todoDescription = event.description
            }

            is AddEditEvent.EnteredDateAndTime -> {
                viewModelScope.launch {
                    if (event.dateAndTime != null) {
                        _eventFlow.emit(UiEvent.TimeIsSet)
                    } else {
                        _eventFlow.emit(UiEvent.TimeNotSet)
                    }
                }

                todoDateAndTime = event.dateAndTime
            }

            is AddEditEvent.EnteredIsDone -> {
                todoIsCompleted = event.isCompleted
            }

            is AddEditEvent.EnteredGroupName -> {
                todoGroupName = event.groupName
            }

            is AddEditEvent.SaveGroup -> {
                viewModelScope.launch {
                    try {
                        todoUseCase.insertGroupTodoEntity(GroupTodoEntity(name = event.groupName))
                        _eventFlow.emit(UiEvent.SaveGroupSuccess)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.SaveGroupFailed)
                    }
                }
            }

            is AddEditEvent.SaveToDo -> {
                saveToDo()
            }

            AddEditEvent.DeleteToDo -> {
                viewModelScope.launch {
                    todoUseCase.deleteTodoEntity(todoUseCase.getTodoById(todoId))
                    _eventFlow.emit(UiEvent.DeleteToDoSuccess)
                }
            }
        }
    }

    private fun saveToDo() {
        if (todoId == -1L) {
            viewModelScope.launch {
                try {
                    todoUseCase.insertTodoEntity(
                        TodoEntity(
                            id = 0,
                            title = todoTitle,
                            description = todoDescription,
                            dateAndTime = todoDateAndTime,
                            isCompleted = todoIsCompleted,
                            isExpired = false,
                            groupName = todoGroupName
                        )
                    )
                    _eventFlow.emit(UiEvent.SaveToDoSuccess)
                } catch (e: Exception) {
                    _eventFlow.emit(UiEvent.SaveToDoFailed)
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    todoUseCase.updateTodoEntity(
                        TodoEntity(
                            id = todoId,
                            title = todoTitle,
                            description = todoDescription,
                            dateAndTime = todoDateAndTime,
                            isCompleted = todoIsCompleted,
                            isExpired = false,
                            groupName = todoGroupName
                        )
                    )
                    _eventFlow.emit(UiEvent.UpdateToDoSuccess)
                } catch (e: Exception) {
                    _eventFlow.emit(UiEvent.UpdateToDoFailed)
                }
            }
        }
    }

    sealed interface UiEvent {
        object SaveGroupSuccess : UiEvent
        object SaveGroupFailed : UiEvent
        object DeleteToDoSuccess : UiEvent
        object SaveToDoSuccess : UiEvent
        object SaveToDoFailed : UiEvent
        object UpdateToDoSuccess : UiEvent
        object UpdateToDoFailed : UiEvent
        object TimeNotSet : UiEvent
        object TimeIsSet : UiEvent
    }
}