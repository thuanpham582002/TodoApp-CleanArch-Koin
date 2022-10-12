package com.example.todoappcleanarchwithkoin.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.core.domain.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.todoappcleanarchwithkoin.ui.notification.constants.ACTION_DELETE
import com.example.todoappcleanarchwithkoin.ui.notification.constants.ACTION_DONE
import com.example.todoappcleanarchwithkoin.ui.notification.constants.TODO_ID
import org.koin.java.KoinJavaComponent.get

class TodoSateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoScheduler: TodoScheduler = get(TodoScheduler::class.java)

        val action = intent.action
        val id = intent.getLongExtra(TODO_ID, -1)
        val todoRepository: TodoRepository = get(TodoRepository::class.java)
        if (ACTION_DONE == action) {
            Log.i("ToDoChangeDoneReceiver", "onReceive: $action")
            CoroutineScope(Dispatchers.IO).launch {
                val toDo = todoRepository.getTodoById(id)
                todoRepository.updateTodo(toDo.copy(isCompleted = true))
            }
        } else if (ACTION_DELETE == action) {
            Log.i("ToDoChangeDoneReceiver", "onReceive: $action $id")
            CoroutineScope(Dispatchers.IO).launch {
                val toDo = todoRepository.getTodoById(id)
                todoRepository.deleteTodo(toDo)
            }
        }
        todoScheduler.todoDeleteNotification(id)
    }
}