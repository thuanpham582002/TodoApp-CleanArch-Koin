package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo
import com.example.core.data.source.local.model.todo.TodoEntity

data class AddEditState(
    val toDo: TodoEntity = TodoEntity(),
    val currentGroupIndex: Int = 0,
)
