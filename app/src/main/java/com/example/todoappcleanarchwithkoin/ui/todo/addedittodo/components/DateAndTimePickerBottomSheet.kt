package com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.components

import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.todoappcleanarchwithkoin.databinding.BottomsheetDateAndTimeBinding
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.utils.ActionSetTime
import com.example.todoappcleanarchwithkoin.ui.todo.util.toFormattedString
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.todoappcleanarchwithkoin.ui.todo.addedittodo.constants.ACTION_SET_TIME_AND_DATE
import java.util.*


class DateAndTimePickerBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetDateAndTimeBinding
    private lateinit var actionSetTime: ActionSetTime

    companion object {
        fun newInstance(actionSetTime: ActionSetTime) = DateAndTimePickerBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(ACTION_SET_TIME_AND_DATE, actionSetTime)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetDateAndTimeBinding.inflate(inflater, container, false)
        binding.timePicker.setIs24HourView(true)
        actionSetTime = arguments?.getSerializable(ACTION_SET_TIME_AND_DATE) as ActionSetTime

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get current date and time
        binding.tvDetailTime.text = getCurrentTimeAndDate().toFormattedString(requireContext())
        setOnClick()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentTimeAndDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return GregorianCalendar(year, month, day, hour, minute).time
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setOnClick() {
        binding.apply {
            btnCancel.setOnClickListener {
                cancelButtonClicked()
            }
            btnOK.setOnClickListener {
                okButtonClicked()
            }
        }
    }


    private fun cancelButtonClicked() = this.dismiss()

    @RequiresApi(Build.VERSION_CODES.N)
    private fun okButtonClicked() {
        actionSetTime.setTime(getDate())
        this.dismiss()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDate(): Date {
        binding.apply {
            val year = datePicker.year
            val month = datePicker.month
            val dayOfMonth = datePicker.dayOfMonth
            val hour = timePicker.hour
            val minute = timePicker.minute
            val date = GregorianCalendar(year, month, dayOfMonth, hour, minute)
            return date.time
        }
    }
}