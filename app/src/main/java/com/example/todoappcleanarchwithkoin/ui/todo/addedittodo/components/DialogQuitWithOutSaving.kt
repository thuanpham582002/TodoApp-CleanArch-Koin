@file:Suppress("unused")

package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.components

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.todoappcleanarchwithkoin.R

@Suppress("unused", "unused")
object DialogQuitWithOutSaving {
    fun show(context: Context, actionQuit: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.are_you_sure_quit_without_saving))
            .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                actionQuit()
            }
            .setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}