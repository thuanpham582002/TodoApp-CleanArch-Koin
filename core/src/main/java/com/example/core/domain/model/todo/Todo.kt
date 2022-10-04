package com.example.core.domain.model.todo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Todo(
    val id: Long,
    val title: String,
    val description: String,
    val dateAndTime: Date?,
    val isCompleted: Boolean,
    val isExpired: Boolean,
    val groupName: String
) : Parcelable

class InvalidTodoException(message: String) : Exception(message)
