package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import java.util.*

interface AddEditEvent {
    data class EnteredTitle(val title: String) : AddEditEvent

    data class EnteredDescription(val description: String) : AddEditEvent

    data class EnteredGroupName(val groupName: String) : AddEditEvent

    data class EnteredDateAndTime(val dateAndTime: Date?) : AddEditEvent
    data class SaveGroup(val groupName: String) : AddEditEvent
    data class EnteredIsDone(val isCompleted: Boolean) : AddEditEvent
    object SaveToDo : AddEditEvent
    object DeleteToDo : AddEditEvent
}
