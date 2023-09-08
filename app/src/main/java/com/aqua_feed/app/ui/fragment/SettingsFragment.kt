package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.FragmentSettingsBinding
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.viewmodel.ParamViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferencesUtils: PreferencesUtils
    private val viewModel: ParamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())

        setupDate()
        setupTime()
        setupViewState()
        setupActions()
        fetchData()
        return binding.root
    }

    private fun setupActions() {
        binding.btnSaveSensor.setOnClickListener {
            val isBattery = binding.switchBattery.isChecked
            val isSolarPanel = binding.switchSolarPanel.isChecked
            val isWaterPH = binding.switchWaterPH.isChecked
            val isWater = binding.switchWater.isChecked
            val isTurbidity = binding.switchTurbidity.isChecked
            val isSalinity = binding.switchSalinity.isChecked

            Utils.showToast(requireContext(), "Successfully Updated")
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

    private fun setupViewState() {
        binding.radioAP.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radioAP.isChecked = true
                binding.radioSTA.isChecked = false
            } else {
                binding.radioAP.isChecked = false
                binding.radioSTA.isChecked = true
            }
        }

        binding.radioSTA.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radioAP.isChecked = false
                binding.radioSTA.isChecked = true
            } else {
                binding.radioAP.isChecked = true
                binding.radioSTA.isChecked = false
            }
        }

        binding.radioGroupDevice.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioWifi -> setFormLayout(binding.layoutWifi)
                R.id.radioDevice -> setFormLayout(binding.layoutDevice)
                R.id.radioSensor -> setFormLayout(binding.layoutSensor)
            }
        }
    }

    private fun setFormLayout(enableLayout: LinearLayout) {
        binding.layoutWifi.visibility = View.GONE
        binding.layoutDevice.visibility = View.GONE
        binding.layoutSensor.visibility = View.GONE
        enableLayout.visibility = View.VISIBLE
    }

    private fun fetchData() {
        viewModel.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ParamsResponse -> {
                    val params = paramResult.params
                    binding.edtFeedingRate.text = Editable.Factory.getInstance().newEditable(params.feedrate.toString())
                    binding.edtAmount.text = Editable.Factory.getInstance().newEditable(params.throwamt.toString())
                    binding.edtThowerSpeed.text = Editable.Factory.getInstance().newEditable(params.bldcspeed.toString())
                    binding.edtDosser.text = Editable.Factory.getInstance().newEditable(params.servospeed.toString())
                    binding.edtDosserDelay.text = Editable.Factory.getInstance().newEditable(params.delaystart.toString())
                    binding.edtDosserStop.text = Editable.Factory.getInstance().newEditable(params.delaystop.toString())
                    binding.feedSensor.text = Editable.Factory.getInstance().newEditable(params.delaystop.toString())
                    binding.header.txtWifiName.text = params.hostname
                    binding.switchBattery.isChecked = params.battstatus == true
                    binding.switchWater.isChecked = params.tempstatus == true
                    binding.switchSolarPanel.isChecked = params.solarpstatus == true
                    binding.switchWaterPH.isChecked = params.phstatus == true
                    binding.switchTurbidity.isChecked = params.turbidstatus == true
                    binding.switchSalinity.isChecked = params.inastatus == true
                }
                is ParamResult.TimeResponse -> {
                    val time = paramResult.time
                    binding.header.txtTime.text = "${time.hour}:${time.minute}"
                }
                is ParamResult.WifiResponse -> {
                    binding.header.txtWifiIP.text = "${paramResult.wifi.wifiIP}\n ${paramResult.wifi.wifiSTASSID}"
                    binding.header.wifiBar.text = "${paramResult.wifi.wifiSTARSSI}%"
                }
                is ParamResult.Error -> {
                    // Handle error if needed
                }

                else -> {}
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}