package com.example.todoappcleanarchwithkoin.ui.todo.addgroup

sealed interface AddGroupEvent {
    data class EnteredGroupName(val name: String) : AddGroupEvent
    class SelectColor(val color: Int?) : AddGroupEvent

    object SaveGroup : AddGroupEvent
}