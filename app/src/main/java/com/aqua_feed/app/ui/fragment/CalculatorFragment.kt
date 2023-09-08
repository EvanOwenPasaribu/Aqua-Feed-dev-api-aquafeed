package com.aqua_feed.app.ui.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqua_feed.app.R
import com.aqua_feed.app.adapter.BlindAdapter
import com.aqua_feed.app.databinding.FragmentCalculatorBinding
import com.aqua_feed.app.models.Blind
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.hideKeyboard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculatorFragment : Fragment() {
    private lateinit var binding : FragmentCalculatorBinding
    private lateinit var preferencesUtils: PreferencesUtils
    private lateinit var adapter: BlindAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireActivity().applicationContext)

        setupAdapter()
        setupTime()
        setupDate()
        setupViewState()
        setupActions()
        return binding.root
    }

    private fun setupActions() {
        binding.btnCalculateFeed.setOnClickListener {
            val initial = binding.edtInitialFeed.text.toString()
            val adg = binding.edtADGFeed.text.toString()
            val ratio = binding.edtRatioFeed.text.toString()
            val mbw = binding.edtMBWFeed.text.toString()
            val feed = binding.edtFeedFeed.text.toString()
            when {
                initial.isEmpty() -> binding.edtInitialFeed.error = "Must be filled"
                adg.isEmpty() -> binding.edtADGFeed.error = "Must be filled"
                ratio.isEmpty() -> binding.edtRatioFeed.error = "Must be filled"
                mbw.isEmpty() -> binding.edtMBWFeed.error = "Must be filled"
                feed.isEmpty() -> binding.edtFeedFeed.error = "Must be filled"
                else -> {
                    val feedPercent = (feed.toDouble() / 100) * 10
                    binding.txtSRFeed.text = (initial.toDouble() * feedPercent * adg.toDouble() * ratio.toDouble() / 1000).toString()
                    binding.txtBiomassFeed.text = (initial.toDouble() * feedPercent * mbw.toDouble() / 1000).toString()
                    binding.txtSizeFeed.text = (1000 / mbw.toDouble()).toString()
                    hideKeyboard()
                }
            }
        }

        binding.btnCalculateSR.setOnClickListener {
            val initial = binding.edtInitialSR.text.toString()
            val adg = binding.edtADGSR.text.toString()
            val ratio = binding.edtRatioSR.text.toString()
            val mbw = binding.edtMBWSR.text.toString()
            val sr = binding.edtSR.text.toString()
            when {
                initial.isEmpty() -> binding.edtInitialSR.error = "Must be filled"
                adg.isEmpty() -> binding.edtADGSR.error = "Must be filled"
                ratio.isEmpty() -> binding.edtRatioSR.error = "Must be filled"
                mbw.isEmpty() -> binding.edtMBWSR.error = "Must be filled"
                sr.isEmpty() -> binding.edtSR.error = "Must be filled"
                else -> {
                    val result1 = ((sr.toDouble() / 100) * 10) * 1000 / initial.toDouble() / adg.toDouble() / ratio.toDouble()
                    binding.txtFeedSR.text = "$result1%"
                    binding.txtBiomassSR.text = (initial.toDouble() * mbw.toDouble() * result1 / 1000).toString()
                    binding.txtSizeSR.text = (1000 / mbw.toDouble()).toString()
                    hideKeyboard()
                }
            }
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val dateCalendar = Calendar.getInstance()
        val datePicker = Utils.showDatePicker(requireContext())
        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            dateCalendar.set(Calendar.YEAR, year)
            dateCalendar.set(Calendar.MONTH, month)
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateValue = dateFormat.format(dateCalendar.time)
            binding.edtStockingDate.setText(dateValue)
        }
        binding.edtStockingDate.setOnClickListener {
            datePicker.show()
        }
        val blinds = arrayListOf<Blind>()
        binding.layoutNoData.visibility = View.VISIBLE
        binding.lineNoData.visibility = View.VISIBLE
        binding.btnCalculateBF.setOnClickListener {
            val pondName = binding.edtPondName.text.toString()
            val initialStocking = binding.edtInitialStocking.text.toString()
            val stockingDate = binding.edtStockingDate.text.toString()
            val sr = binding.edtSRBF.text.toString()
            when {
                pondName.isEmpty() -> binding.edtPondName.error = "Must be filled"
                initialStocking.isEmpty() -> binding.edtInitialStocking.error = "Must be filled"
                stockingDate.isEmpty() -> binding.edtStockingDate.error = "Must be filled"
                sr.isEmpty() -> binding.edtSRBF.error = "Must be filled"
                else -> {
                    /*val initial = initialStocking.toInt()
                    val initialPeriod = 1
                    val fDay = when (initialPeriod) {
                        1 -> 2
                        11 -> 4
                        2 -> 21
                        8 -> 4
                        else -> 4
                    } * initial / 100000*/
                    binding.layoutNoData.visibility = View.GONE
                    binding.lineNoData.visibility = View.GONE
                    blinds.add(Blind(1.0, 2.0, 3.0, 4.0))
                    binding.rvBlindFeeding.removeAllViews()
                    adapter.setBlinds(blinds)
                    adapter.notifyDataSetChanged()
                }
            }
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

    private fun setupAdapter() {
        adapter = BlindAdapter()
        binding.rvBlindFeeding.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBlindFeeding.adapter = adapter
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
        binding.radioFeed.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radioFeed.isChecked = true
                binding.radioBlindFeeding.isChecked = false
                binding.radioSR.isChecked = false
            }
        }

        binding.radioSR.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radioSR.isChecked = true
                binding.radioFeed.isChecked = false
                binding.radioBlindFeeding.isChecked = false
            }
        }

        binding.radioBlindFeeding.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.radioBlindFeeding.isChecked = true
                binding.radioFeed.isChecked = false
                binding.radioSR.isChecked = false
            }
        }

        binding.radioGroupCalculator.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSR -> setFormLayout(binding.layoutSR, false)
                R.id.radioFeed -> setFormLayout(binding.layoutFeed, false)
                R.id.radioBlindFeeding -> setFormLayout(binding.layoutBF, true)
            }
        }
    }

    private fun setFormLayout(enableLayout: LinearLayout, isBlind: Boolean) {
        binding.layoutFeed.visibility = View.GONE
        binding.layoutSR.visibility = View.GONE
        binding.layoutBF.visibility = View.GONE
        enableLayout.visibility = View.VISIBLE

        if (isBlind) {
            binding.btnBlind.visibility = View.VISIBLE
            binding.dataBlind.visibility = View.VISIBLE
        } else {
            binding.btnBlind.visibility = View.GONE
            binding.dataBlind.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalculatorFragment()
    }
}