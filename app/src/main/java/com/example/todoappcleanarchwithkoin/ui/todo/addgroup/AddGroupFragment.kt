package com.example.todoappcleanarchwithkoin.ui.todo.addgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todoappcleanarchwithkoin.databinding.FragmentAddGroupBinding

class AddGroupFragment : Fragment() {
    val binding: FragmentAddGroupBinding by lazy {
        FragmentAddGroupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}