package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.FragmentDeviceInfoBinding
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DeviceInfoFragment : Fragment() {
    private lateinit var binding : FragmentDeviceInfoBinding
    private lateinit var preferencesUtils: PreferencesUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceInfoBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())

        setupDate()
        setupTime()
        setupActions()
        return binding.root
    }

    private fun setupActions() {
        binding.btnOk.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()?.replace(R.id.container, HomeFragment.newInstance())
                ?.commit()
            true
        }

        val selectDeviceDialog = Utils.showDialogSelectDevice(requireContext()) {
            binding.header.txtWifiName.text = preferencesUtils.getWifiName()
            binding.header.txtWifiIP.text = """
                ${preferencesUtils.getWifiIP()}
                SSID CODE
        """.trimIndent()
            binding.txtDeviceIP.text = preferencesUtils.getWifiIP()
            binding.txtDeviceName.text = preferencesUtils.getWifiName()
        }
        binding.header.btnSelectDevice.setOnClickListener {
            selectDeviceDialog.show()
        }

        val wifiName = preferencesUtils.getWifiName()
        val wifiIP = preferencesUtils.getWifiIP()

        binding.txtDeviceIP.text = wifiIP
        binding.txtDeviceName.text = wifiName

        binding.header.txtWifiName.text = wifiName
        binding.header.txtWifiIP.text = """
            $wifiIP
            SSID CODE
        """.trimIndent()
    }

    private fun setupTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val timeCalendar = Calendar.getInstance()
        val userTime = preferencesUtils.getTime()
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            timeCalendar.set(Calendar.MINUTE, minute)

            val timeValue = timeFormat.format(timeCalendar.time)
            binding.header.txtTime.text = timeValue
            preferencesUtils.setTime(timeValue)
        }
        val timePicker = Utils.showTimePicker(requireContext(), timePickerListener)

        binding.header.txtTime.setOnLongClickListener { timePicker.show(); true }
        binding.header.txtTime.text = userTime.ifEmpty {
            timeFormat.format(timeCalendar.time)
        }
    }

    private fun setupDate() {
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy", Locale.US)
        val dateCalendar = Calendar.getInstance()
        val datePicker = Utils.showDatePicker(requireContext())
        val userDate = preferencesUtils.getDate()

        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            dateCalendar.set(Calendar.YEAR, year)
            dateCalendar.set(Calendar.MONTH, month)
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateValue = dateFormat.format(dateCalendar.time)
            binding.header.txtDate.text = dateValue
            preferencesUtils.setDate(dateValue)
        }
        binding.header.txtDate.setOnLongClickListener { datePicker.show(); true }
        binding.header.txtDate.text = userDate.ifEmpty {
            dateFormat.format(dateCalendar.time)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeviceInfoFragment()
    }
}