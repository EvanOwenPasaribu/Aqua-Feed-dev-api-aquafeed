package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aqua_feed.app.databinding.FragmentCalibrationBinding
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.hideKeyboard
import com.aqua_feed.app.viewmodel.ParamViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class CalibrationFragment : Fragment() {
    private lateinit var binding : FragmentCalibrationBinding
    private lateinit var preferencesUtils: PreferencesUtils
    private val viewModel: ParamViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalibrationBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())

        setupDate()
//        setupTime()
        fetchData()
        setupDuration()
        setupFeedingRate()

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
        return binding.root
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

    private fun setupFeedingRate() {
        var countDownTimer: CountDownTimer? = null
        var progressPercentage: Int
        binding.btnStartDuration.setOnClickListener {
            val duration = binding.edtDuration.text.toString()
            when {
                duration.isEmpty() -> binding.edtDuration.error = "Must be filled"
                duration.toInt() < 1 -> binding.edtDuration.error = "Must be greater than 0 seconds"
                else -> {
                    binding.btnCancelDuration.visibility = View.VISIBLE
                    binding.btnStartDuration.visibility = View.GONE
                    binding.progressDuration.progress = 0
                    binding.txtProgress.text = "0%"
                    progressPercentage = 0

                    if (countDownTimer == null) {
                        val durationInSeconds = duration.toInt() * 1_000
                        countDownTimer = object : CountDownTimer(durationInSeconds.toLong(), 1_000) {
                            override fun onTick(millisUntilFinished: Long) {
                                progressPercentage++
                                val progress = progressPercentage * 100 / (durationInSeconds / 1_000)
                                binding.progressDuration.progress = progress
                                binding.txtProgress.text = "$progress%"
                            }

                            override fun onFinish() {
                                binding.progressDuration.progress = 100
                                binding.txtProgress.text = "100%"
                                binding.btnCancelDuration.visibility = View.GONE
                                binding.btnStartDuration.visibility = View.VISIBLE
                                countDownTimer?.cancel()
                                countDownTimer = null
                            }
                        }
                        countDownTimer!!.start()
                    }
                    hideKeyboard()
                }
            }
        }

        binding.btnCancelDuration.setOnClickListener {
            progressPercentage = 0
            countDownTimer?.let {
                it.cancel()
                countDownTimer = null
            }
            binding.progressDuration.progress = 0
            binding.txtProgress.text = "0%"
            binding.btnCancelDuration.visibility = View.GONE
            binding.btnStartDuration.visibility = View.VISIBLE
            hideKeyboard()
        }
    }

    private fun setupDuration() {
        binding.btnCalculateFeeding.setOnClickListener {
            val grams = binding.edtGramsResult.text.toString()
            val duration = binding.edtDuration.text.toString()
            when {
                grams.isEmpty() -> binding.edtGramsResult.error = "Must be filled"
                grams.toInt() < 1 -> binding.edtGramsResult.error = "Must be greater than 0 grams"
                duration.isEmpty() -> binding.edtDuration.error = "Must be filled"
                duration.toInt() < 1 -> binding.edtDuration.error = "Must be greater than 0 seconds"
                else -> {
                    val result = (grams.toDouble() / (duration.toDouble() / 60.0)).roundToInt()
                    binding.edtFeedingRate.setText(result.toString())
                    hideKeyboard()
                }
            }
        }

        binding.btnSaveFeeding.setOnClickListener {
            val grams = binding.edtGramsResult.text.toString()
            val feedingRate = binding.edtFeedingRate.text.toString()
            when {
                grams.isEmpty() -> binding.edtGramsResult.error = "Must be filled"
                grams.toInt() < 1 -> binding.edtGramsResult.error = "Must be greater than 0 grams"
                feedingRate.isEmpty() -> binding.edtFeedingRate.error = "Must be filled"
                feedingRate.toInt() < 1 -> binding.edtFeedingRate.error = "Must be greater than 0 seconds"
                else -> {
                    hideKeyboard()
                }
            }
        }
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
        fun newInstance() = CalibrationFragment()
    }

}