package com.example.todoappcleanarchwithkoin.ui.todo.home.components.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.core.data.source.local.model.todo.TodoEntity
import com.example.todoappcleanarchwithkoin.R
import com.example.todoappcleanarchwithkoin.databinding.ItemTodoBinding
import com.example.todoappcleanarchwithkoin.ui.todo.home.HomeEvent
import com.example.todoappcleanarchwithkoin.ui.todo.home.HomeViewModel
import com.example.todoappcleanarchwithkoin.ui.todo.home.components.util.TodoRecycleDiffUtil
import com.example.todoappcleanarchwithkoin.ui.todo.util.toFormattedString

class TodoAdapter(private val homeViewModel: HomeViewModel) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    var tracker: SelectionTracker<Long>? = null
    var dataList = emptyList<TodoEntity>()

    inner class TodoViewHolder(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoEntity: TodoEntity) {
            bindData(todoEntity)
            onClick(todoEntity)
            checkItemSelection(todoEntity)
            startAnimation()
        }

        private fun bindData(todoEntity: TodoEntity) {
            binding.tvTodo.text = todoEntity.title
            binding.tvGroupName.text = todoEntity.groupName
            binding.tvDateAndTime.text =
                todoEntity.dateAndTime.toFormattedString(context = binding.root.context)
            binding.checkbox.isChecked = todoEntity.isCompleted
        }

        fun getToDoDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long {
                    return dataList[adapterPosition].id
                }

                override fun getPosition(): Int {
                    return adapterPosition
                }
            }
        }

        private fun startAnimation() {
            val animation: Animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.anim_fade_in)
            binding.root.startAnimation(animation)
        }

        fun detachAnimation() {
            binding.root.clearAnimation()
        }

        private fun checkItemSelection(todoEntity: TodoEntity) {
            if (tracker?.isSelected(todoEntity.id) == true) {
                Log.i("adapter", "checkItemSelection: ")
                binding.root.alpha = 0.5f
            } else {
                binding.root.alpha = 1f
            }
        }

        private fun onClick(todoEntity: TodoEntity) {
            binding.root.setOnClickListener {
                homeViewModel.saveStateTodo(todoEntity)
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_addEditFragment)
            }

            binding.checkbox.setOnClickListener {
                homeViewModel.onEvent(HomeEvent.UpdateToDo(todoEntity.copy(isCompleted = binding.checkbox.isChecked)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        Log.i("HomeViewModel", "onBindViewHolder: $position")
        holder.bind(dataList[position])
    }

    override fun onViewDetachedFromWindow(holder: TodoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.detachAnimation()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(todoList: List<TodoEntity>) {
        Log.i("HomeViewModel", "set data ${todoList.size}")
        val diffUtil = TodoRecycleDiffUtil(this.dataList, todoList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.dataList = todoList
        diffResult.dispatchUpdatesTo(this)
    }
}