package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.utils

import java.io.Serializable
import java.util.*

interface ActionSetTime : Serializable {
    fun setTime(dateAndTime: Date?)
}