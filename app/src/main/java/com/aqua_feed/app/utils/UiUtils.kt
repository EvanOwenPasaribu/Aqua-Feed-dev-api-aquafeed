package com.aqua_feed.app.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aqua_feed.app.R
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object UiUtils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showLoadingDialog(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    fun showDateRangeDialog(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_date_range)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
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

    fun showNumberPicker(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_number_picker)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
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

    fun showTimePicker(context: Context, timeSetListener: OnTimeSetListener): TimePickerDialog {
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

    fun checkEmptyData(context: Context, isEmpty: Boolean, image: AppCompatImageView) {
        if (isEmpty) {
            Glide.with(context)
                .load(R.drawable.no_data)
                .into(image)
            image.visibility = View.VISIBLE
        } else {
            image.visibility = View.GONE
        }
    }

    fun showAlertDialog(context: Context, text: String, onPositiveClicked: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(text)
        builder.setPositiveButton("YA") { _, _ ->
            onPositiveClicked.invoke()
        }
        builder.setNegativeButton("BATAL", null)
        builder.show()
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

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}