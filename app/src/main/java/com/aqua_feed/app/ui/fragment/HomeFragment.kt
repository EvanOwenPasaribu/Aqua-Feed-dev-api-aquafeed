package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqua_feed.app.R
import com.aqua_feed.app.adapter.ScheduleShortAdapter
import com.aqua_feed.app.databinding.FragmentHomeBinding
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.Utils.mapToScheduleEntityList
import com.aqua_feed.app.viewmodel.ParamViewModel
import com.aqua_feed.app.viewmodel.ScheduleViewModel
import com.aquafeed.app.model.response.Schedule
import com.aquafeed.app.view.fragment.SensorFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ScheduleShortAdapter
    private lateinit var preferencesUtils: PreferencesUtils
    private val viewModel: ParamViewModel by viewModels()
    private val viewModelSchedule: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireActivity().applicationContext)
        setupList()
        setupDate()
        fetchData()
        viewModelSchedule.getAllSchedule()
        getSchedule()

        val selectDeviceDialog = Utils.showDialogSelectDevice(requireContext()) {
            binding.txtWifiName.text = preferencesUtils.getWifiName()
            binding.txtWifiIP.text = """
                ${preferencesUtils.getWifiIP()}
                SSID CODE
        """.trimIndent()
        }
        binding.btnDeviceInfo.setOnClickListener {
            selectDeviceDialog.show()
        }
        binding.txtWifiName.text = preferencesUtils.getWifiName()
        binding.txtWifiIP.text = """
            ${preferencesUtils.getWifiIP()}
            SSID CODE
        """.trimIndent()

        binding.btnSensor.setOnLongClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()?.replace(R.id.container, SensorFragment.newInstance())
                ?.commit()
            true
        }

        binding.btnWeather.setOnLongClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()?.replace(R.id.container, WeatherFragment.newInstance())
                ?.commit()
            true
        }

        binding.btnEdit.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()?.replace(R.id.container, ScheduleFragment.newInstance())
                ?.commit()
        }
        return binding.root
    }

    private fun fetchData() {
        viewModel.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ParamsResponse -> {
                    val params = paramResult.params
                    binding.txtWifiName.text = params.hostname
                }
                is ParamResult.TimeResponse -> {
                    val time = paramResult.time
                    binding.txtTime.text = "${time.hour}:${time.minute}"
                }
                is ParamResult.WifiResponse -> {
                    binding.txtWifiIP.text = "${paramResult.wifi.wifiIP}\n ${paramResult.wifi.wifiSTASSID}"
                    binding.wifiBar.text = "${paramResult.wifi.wifiSTARSSI}%"
                }
                is ParamResult.StatusResponse -> {
                    binding.messageText.text = Editable.Factory.getInstance().newEditable(paramResult.status.msg)
                }
                is ParamResult.Error -> {
                    // Handle error if needed
                }

                else -> {}
            }
        }
    }

    private fun setupList() {
        adapter = ScheduleShortAdapter()
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = adapter
    }

    private fun setupTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val timeCalendar = Calendar.getInstance()
        val userTime = preferencesUtils.getTime()
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            timeCalendar.set(Calendar.MINUTE, minute)

            val timeValue = timeFormat.format(timeCalendar.time)
            binding.txtTime.text = timeValue
            preferencesUtils.setTime(timeValue)
        }
        val timePicker = Utils.showTimePicker(requireContext(), timePickerListener)

        binding.txtTime.setOnLongClickListener { timePicker.show(); true }
        binding.txtTime.text = userTime.ifEmpty {
            timeFormat.format(timeCalendar.time)
        }
    }

    private fun getSchedule() {
        viewModelSchedule.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ScheduleResponse -> {
                    if (paramResult.schedule.response == "No Data"){
                        Toast.makeText(context, paramResult.schedule.response, Toast.LENGTH_LONG).show()
                    }else{
                        handleScheduleResponse(paramResult.schedule)
                    }
                }
                is ParamResult.Error -> {

                }

                else -> {

                }
            }
        }
    }

    private fun handleScheduleResponse(scheduleData: Schedule) {
        try {
            val scheduleList = mapToScheduleEntityList(scheduleData)
            if (scheduleData.schedules!!.isNotEmpty()) {
                binding.layoutNoData.visibility = View.GONE
                binding.rvSchedule.removeAllViews()
                adapter.setSchedules(scheduleList)
                adapter.notifyDataSetChanged()
                val totalKg = scheduleList.sumOf { it.kg }
                binding.kgText.text = totalKg.toString()
            } else {
                binding.layoutNoData.visibility = View.VISIBLE
            }
        }catch (e: Exception){
            println("Something When Wrong")
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
            binding.txtDate.text = dateValue
            preferencesUtils.setDate(dateValue)
        }
        binding.txtDate.setOnLongClickListener { datePicker.show(); true }
        binding.txtDate.text = userDate.ifEmpty {
            dateFormat.format(dateCalendar.time)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}