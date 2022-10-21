package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todoappcleanarchwithkoin.R
import com.example.todoappcleanarchwithkoin.databinding.BottomsheetAddEditTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddEditBottomSheetFragment : BottomSheetDialogFragment() {
    val binding by lazy { BottomsheetAddEditTodoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}