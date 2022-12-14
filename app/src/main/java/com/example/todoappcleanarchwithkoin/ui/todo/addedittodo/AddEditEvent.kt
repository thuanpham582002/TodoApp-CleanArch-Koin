package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import java.util.*

interface AddEditEvent {
    data class EnteredTitle(val title: String) : AddEditEvent
    data class EnteredColor(val color: Int?) : AddEditEvent
    data class EnteredDate(val date: Date?) : AddEditEvent
    object SaveToDo : AddEditEvent
}
