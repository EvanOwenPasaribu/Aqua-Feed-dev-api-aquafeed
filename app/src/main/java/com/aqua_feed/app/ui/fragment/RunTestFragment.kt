package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.FragmentRunTestBinding
import com.aqua_feed.app.models.request.ControlBLDCRequest
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.hideKeyboard
import com.aqua_feed.app.viewmodel.MotorViewModel
import com.aqua_feed.app.viewmodel.ParamViewModel
import com.aquafeed.app.model.response.FeedingRequest
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RunTestFragment : Fragment() {
    private lateinit var binding: FragmentRunTestBinding
    private lateinit var preferencesUtils: PreferencesUtils
    private val viewModel: ParamViewModel by viewModels()
    private val motorViewModel: MotorViewModel by viewModels()
    private var  motor = "Thrower"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunTestBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())

        setupDate()
//        setupTime()
        motorTest()
        feedingTest()
        setupCommaInKg()
        fetchData()

        setButtonBackgrounds(true)

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

    private fun fetchData() {
        viewModel.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.ParamsResponse -> {
                    val params = paramResult.params
                    binding.header.txtWifiName.text = params.hostname
                    binding.edtFeedingRate.text = Editable.Factory.getInstance().newEditable(params.feedrate.toString())
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

    private fun feedingTest() {
        var countDownTimer: CountDownTimer? = null
        var progressPercentage: Int

        binding.btnStartFeedingTest.setOnClickListener {
            val amount = binding.edtTestAmount.text.toString()
            val feedingRateDuration = binding.edtFeedingRate.text.toString()
            when {
                feedingRateDuration.isEmpty() -> binding.edtFeedingRate.error = "Must be filled"
                feedingRateDuration.toInt() < 1 -> binding.edtFeedingRate.error = "Must be greater than 0 gr/min."
                amount.isEmpty() -> binding.edtTestAmount.error = "Must be filled"
                amount.toInt() < 1 -> binding.edtTestAmount.error = "Must be greater than 0 Kg"
                else -> {
                    binding.btnStopFeedingTest.visibility = View.VISIBLE
                    binding.btnStartFeedingTest.visibility = View.GONE
                    binding.progressMotorTest.progress = 0
                    binding.txtProgress.text = "0%"
                    progressPercentage = 0

                    if (countDownTimer == null) {
                        val durationInSeconds = amount.toInt() * 1_000 / feedingRateDuration.toInt() / 60 * 1_000
                        countDownTimer = object : CountDownTimer(durationInSeconds.toLong(), 1_000) {
                            override fun onTick(millisUntilFinished: Long) {
                                progressPercentage++
                                val progress = progressPercentage * 100 / (durationInSeconds / 1_000)
                                binding.progressMotorTest.progress = progress
                                binding.txtProgress.text = "$progress%"
                            }

                            override fun onFinish() {
                                binding.progressMotorTest.progress = 100
                                binding.txtProgress.text = "100%"
                                binding.btnStopFeedingTest.visibility = View.GONE
                                binding.btnStartFeedingTest.visibility = View.VISIBLE
                                countDownTimer?.cancel()
                                countDownTimer = null
                                motorViewModel.postFeeding(FeedingRequest(kg = 0, status = false))
                            }
                        }
                        countDownTimer!!.start()
                    }
                    motorViewModel.postFeeding(FeedingRequest(kg = amount.toInt(), status = true))
                    hideKeyboard()
                }
            }
        }

        binding.btnStopFeedingTest.setOnClickListener {
            progressPercentage = 0
            countDownTimer?.let {
                it.cancel()
                countDownTimer = null
            }
            binding.progressMotorTest.progress = 0
            binding.txtProgress.text = "0%"
            binding.btnStopFeedingTest.visibility = View.GONE
            binding.btnStartFeedingTest.visibility = View.VISIBLE
            hideKeyboard()
        }
    }

    private fun motorTest() {
        var isMotorStarted = false
        binding.btnThrower.setOnClickListener {
            handleMotorSelection("Thrower")
        }

        binding.btnDosser.setOnClickListener {
            handleMotorSelection("Dosser")
        }

        binding.btnSaveMotorTest.setOnClickListener {
            val speed = binding.edtSpeedMotor.text.toString()
            when {
                motor.isEmpty() -> Utils.showToast(requireContext(), "Choose Your Motor")
                speed.isEmpty() -> binding.edtSpeedMotor.error = "Speed must be filled"
                else -> {
                    // Save Motor
                    isMotorStarted = true
                    hideKeyboard()
                }
            }
        }
        binding.btnStartMotorTest.setOnClickListener {
            val speed = binding.edtSpeedMotor.text.toString()
            when {
                motor.isEmpty() -> Utils.showToast(requireContext(), "Choose Your Motor")
                speed.isEmpty() -> binding.edtSpeedMotor.error = "Speed must be filled"
                else -> {
                    // Start Motor
                    isMotorStarted = true
                    hideKeyboard()
                    if (motor == "Thrower"){
                        motorViewModel.startBldc(ControlBLDCRequest(duration = 100, speed = speed.toInt(),status = isMotorStarted ))
                    }else{
                        motorViewModel.startServo(ControlBLDCRequest(duration = 100, speed = speed.toInt(),status = isMotorStarted ))
                    }
                }
            }
        }

        binding.btnStopMotorTest.setOnClickListener {
            when {
                !isMotorStarted -> Utils.showToast(requireContext(), "Start Your Motor First")
                else -> {
                    // Stop Motor
                    isMotorStarted = false
                    hideKeyboard()
                    if (motor == "Thrower"){
                        motorViewModel.startBldc(ControlBLDCRequest(duration = 30, speed = 60 ,status = isMotorStarted ))
                    }else{
                        motorViewModel.startServo(ControlBLDCRequest(duration = 30, speed = 60 ,status = isMotorStarted ))
                    }
                }
            }
        }

        binding.sliderSpeed.addOnChangeListener { _, value, _ ->
            binding.txtProgressMotor.text = "${value.toInt()}%"
        }
    }

    private fun setupCommaInKg() {
        binding.edtTestAmount.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        binding.edtTestAmount.addTextChangedListener {
            val amount = binding.edtTestAmount.text.toString()
            if (amount.isNotEmpty()) {
                val isDecimal = amount.any { it == '.' }
                if (isDecimal) {
                    val afterComma = amount.substringAfter(".")
                    if (afterComma.length > 1) {
                        val amountText = amount.substring(0, amount.length - 1)
                        binding.edtTestAmount.setText(amountText)
                        binding.edtTestAmount.setSelection(amountText.length)
                        hideKeyboard()
                    }
                }
            }
        }
    }

    private fun setButtonBackgrounds(isThrowerSelected: Boolean) {
        val throwerStateListDrawable = StateListDrawable()
        val dosserStateListDrawable = StateListDrawable()

        val pressedBorderColor = ContextCompat.getColor(requireContext(), R.color.green_A700)
        val unpressedBorderColor = ContextCompat.getColor(requireContext(), R.color.bluegray_100)

        val pressedShape = GradientDrawable()
        pressedShape.shape = GradientDrawable.RECTANGLE
        pressedShape.setColor(Color.TRANSPARENT)
        pressedShape.cornerRadius = resources.getDimension(R.dimen._5pxh)
        pressedShape.setStroke(resources.getDimensionPixelSize(R.dimen._1pxh), pressedBorderColor)

        val unpressedShape = GradientDrawable()
        unpressedShape.shape = GradientDrawable.RECTANGLE
        unpressedShape.setColor(Color.TRANSPARENT)
        unpressedShape.cornerRadius = resources.getDimension(R.dimen._5pxh)
        unpressedShape.setStroke(
            resources.getDimensionPixelSize(R.dimen._1pxh),
            unpressedBorderColor
        )

        throwerStateListDrawable.addState(intArrayOf(android.R.attr.state_selected), pressedShape)
        throwerStateListDrawable.addState(intArrayOf(), unpressedShape)

        dosserStateListDrawable.addState(intArrayOf(android.R.attr.state_selected), pressedShape)
        dosserStateListDrawable.addState(intArrayOf(), unpressedShape)

        binding.btnThrower.isSelected = isThrowerSelected
        binding.btnDosser.isSelected = !isThrowerSelected

        binding.btnThrower.background = throwerStateListDrawable
        binding.btnDosser.background = dosserStateListDrawable
    }


    private fun handleMotorSelection(selectedMotor: String) {
        if (selectedMotor == "Thrower") {
            setButtonBackgrounds(true)
            motor = "Thrower"
        } else if (selectedMotor == "Dosser") {
            setButtonBackgrounds(false)
            motor = "Dosser"
        }
        Toast.makeText(requireContext(), "Selected Motor: $motor", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = RunTestFragment()
    }

}