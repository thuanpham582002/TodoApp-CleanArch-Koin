package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.source.local.model.group.GroupTodoEntity
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.core.domain.model.group.InvalidGroupException
import com.example.core.domain.model.todo.InvalidTodoException
import com.example.core.domain.use_case.TodoUseCase
import com.example.todoappcleanarchwithkoin.R
import com.example.todoappcleanarchwithkoin.ui.notification.TodoScheduler
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.constants.BACK_TO_PREVIOUS_SCREEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get
import java.util.*

class AddEditViewModel(
    private val todoUseCase: TodoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val coreTodoEntity = savedStateHandle.get<TodoEntity>("todoEntity")
    private val todoScheduler: TodoScheduler = get(TodoScheduler::class.java)

    var todoIsCompleted: Boolean = false
    var todoId: Long = 0
    var todoGroupName: String = "Default"
    var todoDescription: String = ""
    var todoDateAndTime: Date? = null
    var todoTitle: String = ""
    val listGroup = todoUseCase.getAllGroupTodoEntity()
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (coreTodoEntity!!.title.isNotEmpty()) {
            todoId = coreTodoEntity.id
            todoTitle = coreTodoEntity.title
            todoDescription = coreTodoEntity.description
            todoIsCompleted = coreTodoEntity.isCompleted
            todoDateAndTime = coreTodoEntity.dateAndTime
            todoGroupName = coreTodoEntity.groupName
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
                todoDateAndTime = event.dateAndTime
            }

            is AddEditEvent.EnteredIsDone -> {
                todoIsCompleted = event.isCompleted
            }

            is AddEditEvent.EnteredGroupName -> {
                todoGroupName = event.groupName
            }

            is AddEditEvent.SaveGroup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        todoUseCase.insertGroupTodoEntity(GroupTodoEntity(name = event.groupName))
                        _eventFlow.emit(UiEvent.Message(R.string.group_saved))
                    } catch (e: InvalidGroupException) {
                        _eventFlow.emit(UiEvent.Message(R.string.invalid_group))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.Message(R.string.group_not_saved))
                    }
                }
            }

            is AddEditEvent.SaveToDo -> {
                saveToDo()
            }

            AddEditEvent.DeleteToDo -> {
                viewModelScope.launch(Dispatchers.IO) {
                    todoUseCase.deleteTodoEntity(todoUseCase.getTodoById(todoId))
                    _eventFlow.emit(
                        UiEvent.ChangeUi(
                            BACK_TO_PREVIOUS_SCREEN, R.string.todo_deleted
                        )
                    )
                    todoScheduler.todoCancelAlarmManager(todoId)
                }
            }
        }
    }

    private fun saveToDo() {
        viewModelScope.launch {
            try {
                if (coreTodoEntity!!.title.isEmpty()) {
                    todoId = todoUseCase.insertTodoEntity(getCurrentTodo())
                    _eventFlow.emit(UiEvent.ChangeUi(BACK_TO_PREVIOUS_SCREEN, R.string.todo_saved))
                } else {
                    todoUseCase.updateTodoEntity(getCurrentTodo())
                    _eventFlow.emit(
                        UiEvent.ChangeUi(BACK_TO_PREVIOUS_SCREEN, R.string.todo_updated)
                    )
                }
                Log.i("AddEditViewmodel", "saveToDo: ${getCurrentTodo()}")
                todoScheduler.todoScheduleNotification(getCurrentTodo())
            } catch (e: InvalidTodoException) {
                _eventFlow.emit(UiEvent.Message(R.string.invalid_todo))
            } catch (e: Exception) {
                Log.i("AddEditViewmodel", "saveToDo: ${e.message}")
                _eventFlow.emit(UiEvent.Message(R.string.todo_not_saved))
            }
        }
    }

    sealed interface UiEvent {
        data class Message(val idMessage: Int) : UiEvent
        data class ChangeUi(val id: Int, val idMessage: Int) : UiEvent
    }
}