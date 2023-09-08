package com.aqua_feed.app.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aqua_feed.app.R
import com.aqua_feed.app.models.ScheduleEntity
import com.aquafeed.app.model.response.Schedule
import com.google.android.material.button.MaterialButton
import java.util.Calendar
import java.util.TimeZone

object Utils {

    fun showLoadingDialog(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    fun showDialogReport(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_report)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
    }

    fun showNumberPicker(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_number_picker)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
    }

    fun Long.toLocalCalendar(): Calendar? {
        if (this == 0L) {
            return null
        }
        val rawCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = this@toLocalCalendar
        }
        return Calendar.getInstance().apply {
            clear()
            set(
                rawCalendar.get(Calendar.YEAR),
                rawCalendar.get(Calendar.MONTH),
                rawCalendar.get(Calendar.DAY_OF_MONTH),
                rawCalendar.get(Calendar.HOUR_OF_DAY),
                rawCalendar.get(Calendar.MINUTE),
                rawCalendar.get(Calendar.SECOND),
            )
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showDatePicker(context: Context): DatePickerDialog {
        val calendar = Calendar.getInstance()
        return DatePickerDialog(
            context,
            R.style.DialogTheme,
            null,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    fun showTimePicker(context: Context, timeSetListener: TimePickerDialog.OnTimeSetListener): TimePickerDialog {
        val calendar = Calendar.getInstance()
        return TimePickerDialog(
            context,
            R.style.DialogTheme,
            timeSetListener,
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            true
        )
    }
    fun showDialogSelectDevice(context: Context, onClick: () -> Unit): Dialog {
        val preferencesUtils = PreferencesUtils(context)
        val selectDeviceDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_select_device)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        val btnConnect = selectDeviceDialog.findViewById<MaterialButton>(R.id.btnConnect)
        btnConnect.setOnClickListener {
            selectDeviceDialog.dismiss()
            onClick()
        }

        val btnWifi1 = selectDeviceDialog.findViewById<LinearLayout>(R.id.btnWifi1)
        val btnWifi2 = selectDeviceDialog.findViewById<LinearLayout>(R.id.btnWifi2)
        val btnWifi3 = selectDeviceDialog.findViewById<LinearLayout>(R.id.btnWifi3)

        btnWifi1.setOnClickListener {
            preferencesUtils.setWifi("Aquafeed001", "192.168.0.100")
            btnWifi1.setBackgroundColor(ContextCompat.getColor(context, R.color.black_grey))
            btnWifi2.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            btnWifi3.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
        }

        btnWifi2.setOnClickListener {
            preferencesUtils.setWifi("Aquafeed002", "192.168.0.101")
            btnWifi1.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            btnWifi2.setBackgroundColor(ContextCompat.getColor(context, R.color.black_grey))
            btnWifi3.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
        }

        btnWifi3.setOnClickListener {
            preferencesUtils.setWifi("Aquafeed003", "192.168.0.102")
            btnWifi1.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            btnWifi2.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            btnWifi3.setBackgroundColor(ContextCompat.getColor(context, R.color.black_grey))
        }
        return selectDeviceDialog
    }

    fun mapToScheduleEntityList(schedule: Schedule): List<ScheduleEntity> {
        return schedule.schedules!!.map { scheduleX ->
            ScheduleEntity(
                id = scheduleX.schedId,
                date = "",
                time = scheduleX.schedTime,
                duration = scheduleX.schedDuration,
                kg = scheduleX.schedKg,
                progress = scheduleX.schedProgress,
                status = scheduleX.schedStatus
            )
        }
    }
}