package com.example.core.data.source.local.model.todo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "todo_table")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1L,
    @ColumnInfo(name = "todo_title")
    val title: String = "",
    @ColumnInfo(name = "todo_description")
    val description: String = "",
    @ColumnInfo(name = "todo_date_and_time")
    val dateAndTime: Date? = null,
    @ColumnInfo(name = "todo_is_completed")
    val isCompleted: Boolean = false,
    val isExpired: Boolean = false,
    @ColumnInfo(name = "group_name")
    val groupName: String = "Default"
)  : Parcelable

