package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoappcleanarchwithkoin.R
import com.example.todoappcleanarchwithkoin.databinding.FragmentAddEditBinding
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.components.AlertDeleteBottomSheet
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.components.DateAndTimePickerBottomSheet
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.components.DialogAddGroup
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.utils.ActionDeleteToDo
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.utils.ActionSetTime
import com.example.todoappcleanarchwithkoin.ui.todo.util.toFormattedString
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.constants.BACK_TO_PREVIOUS_SCREEN
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.constants.TIME_NOT_SET
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.constants.TIME_SET
import java.util.*

class AddEditFragment : Fragment(), ActionDeleteToDo, ActionSetTime {
    private var listGroup: List<String> = emptyList()
    private lateinit var binding: FragmentAddEditBinding
    private lateinit var listPopUpGroup: ListPopupWindow
    private lateinit var alertDeleteBottomSheet: AlertDeleteBottomSheet
    private lateinit var dateAndTimePickerBottomSheet: DateAndTimePickerBottomSheet
    private val addEditViewModel: AddEditViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditBinding.inflate(inflater, container, false)
        alertDeleteBottomSheet = AlertDeleteBottomSheet.newInstance(this)
        dateAndTimePickerBottomSheet = DateAndTimePickerBottomSheet.newInstance(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListPopUpGroup()
        setData()
        onUIClick()
        subscribeToObservers()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            addEditViewModel.eventFlow.collectLatest { state ->
                when (state) {
                    is AddEditViewModel.UiEvent.Message -> {
                        showSnackBar(resources.getString(state.idMessage))
                    }
                    is AddEditViewModel.UiEvent.ChangeUi -> {
                        when (state.id) {
                            BACK_TO_PREVIOUS_SCREEN -> {
                                showSnackBar(resources.getString(state.idMessage))
                                findNavController().popBackStack()
                            }
                            TIME_SET -> {
                                showSnackBar(resources.getString(state.idMessage))
                                binding.deleteDateAndTime.visibility = View.GONE
                                val img = requireContext().getDrawable(R.drawable.ic_timer)
                                binding.tvTimeAndDate.icon = img
                            }
                            TIME_NOT_SET -> {
                                showSnackBar(resources.getString(state.idMessage))
                                binding.deleteDateAndTime.visibility = View.VISIBLE
                                val img = requireContext().getDrawable(R.drawable.ic_timer_off)
                                binding.tvTimeAndDate.icon = img
                            }
                        }
                    }
                }
            }
        }

    }

    private fun onUIClick() {
        binding.apply {
            etTitle.doAfterTextChanged { s ->
                addEditViewModel.onEvent(AddEditEvent.EnteredTitle(s.toString()))
            }
            etDescription.doAfterTextChanged { s ->
                addEditViewModel.onEvent(AddEditEvent.EnteredDescription(s.toString()))
            }

            tvGroup.setOnClickListener {
                listPopUpGroup.show()
            }

            tvTimeAndDate.setOnClickListener {
                if (dateAndTimePickerBottomSheet.isAdded) {
                    dateAndTimePickerBottomSheet.dismiss()
                } else {
                    dateAndTimePickerBottomSheet.show(
                        childFragmentManager,
                        "DateAndTimePickerBottomSheet"
                    )
                }
            }

            deleteDateAndTime.setOnClickListener {
                tvTimeAndDate.text = resources.getString(R.string.time_not_set)
                addEditViewModel.onEvent(AddEditEvent.EnteredDateAndTime(null))
                it.visibility = View.GONE
            }

            switchIsDone.setOnCheckedChangeListener { _, isChecked ->
                addEditViewModel.onEvent(AddEditEvent.EnteredIsDone(isChecked))
            }

            btnAddGroup.setOnClickListener {
                DialogAddGroup.show(requireContext()) { groupName ->
                    addEditViewModel.onEvent(AddEditEvent.SaveGroup(groupName))
                }
            }
            onClickToolBar()

            btnDelete.setOnClickListener {
                if (alertDeleteBottomSheet.isAdded) {
                    alertDeleteBottomSheet.dismiss()
                } else {
                    alertDeleteBottomSheet.show(childFragmentManager, "AlertDeleteBottomSheet")
                }
            }
        }
    }

    private fun onClickToolBar() {
        binding.apply {
            toolbar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        addEditViewModel.onEvent(AddEditEvent.SaveToDo)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setData() {
        binding.apply {
            deleteDateAndTime.visibility = if (addEditViewModel.todoDateAndTime == null) View.GONE else View.VISIBLE
            etTitle.setText(addEditViewModel.todoTitle)
            etDescription.setText(addEditViewModel.todoDescription)
            tvTimeAndDate.text =
                addEditViewModel.todoDateAndTime.toFormattedString(requireContext())
            tvGroup.text = addEditViewModel.todoGroupName
            if (addEditViewModel.todoId == -1L) {
                btnDelete.visibility = View.GONE
            }
            switchIsDone.isChecked = addEditViewModel.todoIsCompleted
        }
    }

    private fun setupListPopUpGroup() {
        lifecycleScope.launchWhenStarted {
            addEditViewModel.listGroup.collect {
                listGroup = it.map { groupToDo -> groupToDo.name }
                if ("Default" !in listGroup) {
                    listGroup = listOf("Default") + listGroup
                    addEditViewModel.onEvent(AddEditEvent.SaveGroup("Default"))
                }

                listPopUpGroup = ListPopupWindow(requireContext())
                listPopUpGroup.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        listGroup
                    )
                )
                listPopUpGroup.anchorView = binding.tvGroup
                listPopUpGroup.setOnItemClickListener { _, _, position, _ ->
                    binding.tvGroup.text = listGroup[position]
                    addEditViewModel.onEvent(AddEditEvent.EnteredGroupName(listGroup[position]))
                    listPopUpGroup.dismiss()
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.toolbar.title = resources.getString(R.string.my_to_do)
            toolbar.toolbar.inflateMenu(R.menu.menu_save)
            // display back button
            toolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        }
    }

    override fun deleteToDo() {
        addEditViewModel.onEvent(AddEditEvent.DeleteToDo)
    }

    override fun setTime(dateAndTime: Date?) {
        binding.tvTimeAndDate.text = dateAndTime.toFormattedString(requireContext())
        addEditViewModel.onEvent(AddEditEvent.EnteredDateAndTime(dateAndTime))
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}