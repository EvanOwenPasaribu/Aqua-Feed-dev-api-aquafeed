package com.aqua_feed.app.ui.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqua_feed.app.R
import com.aqua_feed.app.adapter.ScheduleAdapter
import com.aqua_feed.app.databinding.FragmentScheduleBinding
import com.aqua_feed.app.models.ScheduleEntity
import com.aqua_feed.app.models.request.ScheduleDeleteRequest
import com.aqua_feed.app.models.request.ScheduleRequest
import com.aqua_feed.app.models.request.ScheduleUpdateRequest
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.Utils.mapToScheduleEntityList
import com.aqua_feed.app.utils.hideKeyboard
import com.aqua_feed.app.viewmodel.ParamViewModel
import com.aqua_feed.app.viewmodel.ScheduleViewModel
import com.aquafeed.app.model.response.Schedule
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private lateinit var adapter: ScheduleAdapter
    private lateinit var preferencesUtils: PreferencesUtils
    private lateinit var loadingDialog: Dialog
    private val schedules = arrayListOf<ScheduleEntity>()
    private val viewModel: ParamViewModel by viewModels()
    private val viewModelSchedule: ScheduleViewModel by viewModels()
    private var time = ""
    private var editedId = 0
    private var isNextDaySelected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())
        loadingDialog = Utils.showLoadingDialog(requireContext())
        loadingDialog.show()

        setupDate()
        setupList()
        setupEditState()
        setupAutoGenerate()
        setupDurationPicker()
        setupCommaInKg()
        setupActions()
        setupTemplate()
        fetchData()
        getAllSchedule()
        refreshData()
        return binding.root
    }

    private fun setupTemplate() {
        val templates = arrayListOf("Template 1", "Template 2", "Template 3")
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, templates)
        binding.edtTemplateName.setAdapter(arrayAdapter)
        binding.edtTemplateName.setOnClickListener {
            binding.edtTemplateName.showDropDown()
        }
    }

    private fun setupCommaInKg() {
        binding.edtAmount.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        binding.edtAmount.addTextChangedListener {
            val amount = binding.edtAmount.text.toString()
            if (amount.isNotEmpty()) {
                val isDecimal = amount.any { it == '.' }
                if (isDecimal) {
                    val afterComma = amount.substringAfter(".")
                    if (afterComma.length > 1) {
                        val amountText = amount.substring(0, amount.length - 1)
                        binding.edtAmount.setText(amountText)
                        binding.edtAmount.setSelection(amountText.length)
                        hideKeyboard()
                    }
                }
            }
        }
    }

    private fun setupActions() {
//        binding.btnDeleteAll.setOnClickListener { viewModelSchedule.deleteAllSchedule() }
        binding.btnDeleteAll.setOnClickListener {
            val schedId = "-1${binding.edtId.text}"
            if (isNextDaySelected) {
                viewModelSchedule.deleteNextSchedule(ScheduleDeleteRequest(schedId = schedId.toInt()))
            } else {
                viewModelSchedule.deleteSchedule(ScheduleDeleteRequest(schedId = schedId.toInt()))
            }
            resetEditState()
            refreshData()
        }
        binding.btnClear.setOnClickListener {
            binding.edtAmount.setText("")
            binding.edtTime.setText("")
            binding.edtDuration.setText("")
            binding.edtId.setText("")

            binding.btnUpdate.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
            binding.btnAdd.visibility = View.VISIBLE
        }

        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                time = timeFormat.format(calendar.time)

                binding.edtTime.setText(time)
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            true
        )
        binding.edtTime.setOnClickListener { timePicker.show() }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val lastScheduleTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        val dateTomorrow = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
        }
        val dateToday = Date()

        if (preferencesUtils.isNextDaySelected()) {
            binding.radioTomorrow.isChecked = true
            isNextDaySelected = true
        } else {
            binding.radioToday.isChecked = true
            isNextDaySelected = false
        }
        binding.radioSchedule.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioToday -> {
                    isNextDaySelected = false
                    preferencesUtils.setNextDaySelected(false)
                }
                R.id.radioTomorrow -> {
                    isNextDaySelected = true
                    preferencesUtils.setNextDaySelected(true)
                }
            }
            refreshData()
        }

        binding.btnAdd.setOnClickListener {
            val schedule = schedules.lastOrNull()
            calendar.time = dateToday
            val lastSchedule = if (schedule != null) {
                if (schedule.date == dateFormat.format(dateToday)) {
                    schedule.time
                } else null
            } else null
            lastSchedule?.let {
                lastScheduleTime.time = timeFormat.parse(lastSchedule)
            }


            val time = binding.edtTime.text.toString()
            val duration = binding.edtDuration.text.toString()
            val kg = binding.edtAmount.text.toString()
            val scheduleDay = resources.getResourceEntryName(binding.radioSchedule.checkedRadioButtonId)
            val date = if (scheduleDay == "Today") dateFormat.format(dateToday) else dateFormat.format(dateTomorrow.time)

            when {
                time.isEmpty() -> binding.edtTime.error = "Must be filled"
                duration.isEmpty() -> binding.edtDuration.error = "Must be filled"
                kg.isEmpty() -> binding.edtAmount.error = "Must be filled"
                kg.toDouble() < 1 -> binding.edtAmount.error = "Must be greater than 1kg"
                else -> {
                    val takeKg = (kg.toDouble() * 10.0).roundToInt() / 10.0
                    val request = ScheduleRequest(
                        schedKg = takeKg,
                        schedDuration = duration.toInt(),
                        schedStatus = true,
                        schedTime = time
                    )
                    val scheduleEntity = ScheduleEntity(
                        duration = duration.toInt(),
                        kg = takeKg,
                        progress = 0,
                        status = true,
                        time = time,
                        id = 0,
                        date = date
                    )

                        if (isNextDaySelected) {
                            viewModelSchedule.postNextSchedule(request)
                        } else {
                            viewModelSchedule.postSchedule(request)
                        }
                        resetEditState()
                        refreshData()
                    hideKeyboard()
                }
            }
        }


        binding.btnRefresh.setOnClickListener {
            refreshData()
        }

        val selectDeviceDialog = Utils.showDialogSelectDevice(requireContext()) {
            binding.header.txtWifiName.text = preferencesUtils.getWifiName()
            binding.header.txtWifiIP.text = """
                ${preferencesUtils.getWifiIP()}
                SSID CODE
        """.trimIndent()
        }
        binding.header.btnSelectDevice.setOnClickListener {
            selectDeviceDialog.show()
        }
        binding.header.txtWifiName.text = preferencesUtils.getWifiName()
        binding.header.txtWifiIP.text = """
            ${preferencesUtils.getWifiIP()}
            SSID CODE
        """.trimIndent()
    }

    private fun refreshData() {
        if (isNextDaySelected) {
            viewModelSchedule.getAllNextSchedule()
        } else {
            viewModelSchedule.getAllSchedule()
        }
        resetEditState()
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

    private fun setupAutoGenerate() {
        binding.btnCreateTemplate.setOnClickListener {
            val kg = binding.edtAutoGenerateKG.text.toString()
            when {
                kg.isEmpty() -> binding.edtAutoGenerateKG.error = "Must be filled"
                kg.toDouble() < 1.0 -> binding.edtAutoGenerateKG.error = "Must be greater than 0 Kg"
                else -> {
                    val takeKg = (kg.toDouble() * 10.0).roundToInt() / 10.0
                    binding.edtAutoGenerateKG.setText("")
                    hideKeyboard()
                }
            }
        }
    }

    private fun setupDurationPicker() {
        val numberPickerDialog = Utils.showNumberPicker(requireContext())
        val numberPicker = numberPickerDialog.findViewById<NumberPicker>(R.id.numberPicker)
        val btnSetDuration = numberPickerDialog.findViewById<MaterialButton>(R.id.btnSetDuration)
        numberPicker.minValue = 1
        numberPicker.maxValue = 60
        btnSetDuration.setOnClickListener {
            binding.edtDuration.setText(numberPicker.value.toString())
            numberPickerDialog.dismiss()
        }

        binding.edtDuration.setOnClickListener {
            numberPickerDialog.show()
        }
    }

    private fun setupEditState() {
        binding.btnDelete.setOnClickListener {
            val schedId = binding.edtId.text.toString()
            if (isNextDaySelected) {
                viewModelSchedule.deleteNextSchedule(ScheduleDeleteRequest(schedId = schedId.toInt()))
            } else {
                viewModelSchedule.deleteSchedule(ScheduleDeleteRequest(schedId = schedId.toInt()))
            }
            resetEditState()
            refreshData()
        }

        binding.btnUpdate.setOnClickListener {
            val amount = binding.edtAmount.text.toString()
            val edtTime = binding.edtTime.text.toString()
            val duration = binding.edtDuration.text.toString()
            when {
                amount.isEmpty() -> binding.edtAmount.error = "Must be filled"
                amount.toDouble() < 1.0 -> binding.edtAmount.error = "Must be greater than 0 Kg"
                else -> {
                    val takeKg = (amount.toDouble() * 10.0).roundToInt() / 10.0
                    val id = binding.edtId.text.toString().toInt()
                    if (isNextDaySelected) {
                        viewModelSchedule.putNextSchedule(
                            ScheduleUpdateRequest(
                                schedId = id,
                                schedKg = takeKg,
                                schedTime = edtTime,
                                schedStatus = true,
                                schedDuration = duration.toInt(),
                                schedProgress = 0
                            )
                        )
                    } else {
                        viewModelSchedule.putSchedule(
                            ScheduleUpdateRequest(
                                schedId = id,
                                schedKg = takeKg,
                                schedTime = edtTime,
                                schedStatus = true,
                                schedDuration = duration.toInt(),
                                schedProgress = 0
                            )
                        )
                    }
                    refreshData()
                    resetEditState()
                }
            }
        }
    }


    private fun getAllSchedule() {
        viewModelSchedule.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ScheduleResponse -> {
                        if (paramResult.schedule.response == "No Data"){
                            Toast.makeText(context, paramResult.schedule.response, Toast.LENGTH_LONG).show()
                            loadingDialog.dismiss()
                            binding.layoutNoData.visibility = View.VISIBLE
                            binding.lineNoData.visibility = View.VISIBLE
                        }else{
                            handleScheduleResponse(paramResult.schedule)
                        }
                }
                is ParamResult.NextScheduleResponse -> handleScheduleResponse(paramResult.nextSchedule)
                is ParamResult.PostScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
                }
                is ParamResult.NextPostScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
                }
                is ParamResult.PutScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
                }
                is ParamResult.NextPutScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
                }
                is ParamResult.DeleteScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
                }
                is ParamResult.NextDeleteScheduleResponse -> {
                    val message = paramResult.message
                    Utils.showToast(requireContext(), message.response)
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
                binding.lineNoData.visibility = View.GONE
                binding.rvSchedule.removeAllViews()
                adapter.setSchedules(scheduleList)
                adapter.notifyDataSetChanged()
                val totalKg = scheduleList.sumOf { it.kg }
                binding.txtTotalKG.text = totalKg.toString()
            } else {
                binding.layoutNoData.visibility = View.VISIBLE
                binding.lineNoData.visibility = View.VISIBLE
            }
            loadingDialog.dismiss()
        }catch (e: Exception){
            loadingDialog.dismiss()
            binding.layoutNoData.visibility = View.VISIBLE
            binding.lineNoData.visibility = View.VISIBLE
            println("Something When Wrong")
        }
    }

    private fun setupList() {
        adapter = ScheduleAdapter({ schedule, no ->
            binding.btnAdd.visibility = View.GONE
            binding.btnUpdate.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.VISIBLE
            editedId = schedule.id
            binding.edtId.setText(no.toString())
            binding.edtTime.setText(schedule.time)
            binding.edtDuration.setText(schedule.duration.toString())
            binding.edtAmount.setText(schedule.kg.toString())
        }, { id, isChecked ->
        })

        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = adapter
    }

    private fun fetchData() {
        viewModel.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ParamsResponse -> {
                    val params = paramResult.params
                    binding.header.txtWifiName.text = params.hostname
                }

                is ParamResult.TimeResponse -> {
                    val time = paramResult.time
                    binding.header.txtTime.text = "${time.hour}:${time.minute}"
                }

                is ParamResult.WifiResponse -> {
                    binding.header.txtWifiIP.text =
                        "${paramResult.wifi.wifiIP}\n ${paramResult.wifi.wifiSTASSID}"
                    binding.header.wifiBar.text = "${paramResult.wifi.wifiSTARSSI}%"
                }

                is ParamResult.Error -> {
                    // Handle error if needed
                }

                else -> {}
            }
        }
    }


    private fun resetEditState() {
        binding.edtId.setText("")
        binding.edtTime.setText("")
        binding.edtDuration.setText("")
        binding.edtAmount.setText("")
        binding.btnDelete.visibility = View.GONE
        binding.btnUpdate.visibility = View.GONE
        binding.btnAdd.visibility = View.VISIBLE
//        refreshData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleFragment()
    }

}