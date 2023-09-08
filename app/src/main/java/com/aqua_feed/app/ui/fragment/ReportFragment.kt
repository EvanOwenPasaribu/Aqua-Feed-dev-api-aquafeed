package com.aqua_feed.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aqua_feed.app.R
import com.aqua_feed.app.adapter.DialogReportAdapter
import com.aqua_feed.app.adapter.ReportAdapter
import com.aqua_feed.app.callback.FetchRecyclerViewItems
import com.aqua_feed.app.databinding.FragmentReportBinding
import com.aqua_feed.app.models.LogEntry
import com.aqua_feed.app.models.LogEntryEntity
import com.aqua_feed.app.models.request.LogRequest
import com.aqua_feed.app.utils.ParamResult
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aqua_feed.app.utils.toLocalCalendar
import com.aqua_feed.app.viewmodel.LogViewModel
import com.aqua_feed.app.viewmodel.ParamViewModel
import com.aquafeed.app.model.response.LogResponseX
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class ReportFragment : Fragment(), FetchRecyclerViewItems {
    private lateinit var binding: FragmentReportBinding
    private lateinit var preferencesUtils: PreferencesUtils
    private lateinit var adapter: ReportAdapter
    private val viewModel: ParamViewModel by viewModels()
    private val viewModelLog: LogViewModel by viewModels()
    private var serialNumber : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        preferencesUtils = PreferencesUtils(requireContext())
        adapter = ReportAdapter(this)
        setupDate()
        fetchData()
        setupActions()
        fetchLog()
        return binding.root
    }

    private fun setupActions() {

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        var startDate = ""
        var endDate = ""
        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Filter Report")
            .build()
        datePicker.addOnPositiveButtonClickListener { utcMillis ->
            utcMillis.first.toLocalCalendar()?.time?.let {
                startDate = dateFormat.format(it)
            }
            utcMillis.second.toLocalCalendar()?.time?.let {
                endDate = dateFormat.format(it)
            }
            binding.edtRange.setText("$startDate - $endDate")
        }

        binding.edtRange.setOnClickListener {
            datePicker.show(
                (activity as AppCompatActivity).supportFragmentManager,
                "Date Range Filter"
            )
        }
        binding.btnSearch.setOnClickListener {
            when {
                startDate.isEmpty() -> Utils.showToast(
                    requireContext(),
                    "Harap pilih tangal terlebih dahulu"
                )

                endDate.isEmpty() -> Utils.showToast(
                    requireContext(),
                    "Harap pilih tangal terlebih dahulu"
                )

                else -> {
                }
            }
        }

        binding.radioDate.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioRange) {
                binding.layoutSearch.visibility = View.VISIBLE
            } else {
                binding.layoutSearch.visibility = View.GONE
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

    private fun fetchLog() {
        viewModelLog.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.LogResponse -> {
                    paramResult.log.let {
                        binding.rvReports.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvReports.adapter = adapter
                        adapter.setLogs(it)
                    }
                }

                is ParamResult.Error -> {
                    // Handle error if needed
                }

                else -> {}
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
                    binding.header.txtWifiIP.text =
                        "${paramResult.wifi.wifiIP}\n ${paramResult.wifi.wifiSTASSID}"
                    binding.header.wifiBar.text = "${paramResult.wifi.wifiSTARSSI}%"
                }

                is ParamResult.ResponseDevice -> {
                    serialNumber = paramResult.device.serial_number
                }

                is ParamResult.Error -> {
                    // Handle error if needed
                }

                else -> {}
            }
        }
    }

    private fun showDialogReport(logs: LogResponseX) {
        val reportDialog = Utils.showDialogReport(requireContext())
        val rvReportsDialog = reportDialog.findViewById<RecyclerView>(R.id.rvReportsDialog)
        val adapterDialog = DialogReportAdapter()

        rvReportsDialog.layoutManager = LinearLayoutManager(requireContext())
        rvReportsDialog.adapter = adapterDialog

        val date = logs.date
        fetchDataForDialog(date, serialNumber, adapterDialog)
        reportDialog.show()
    }

    private fun extractDataFromLogs(logs: List<String>): List<LogEntry> {
        return logs.map { logItem ->
            val (schid, subschid, timeText, kgamt) = logItem.split(",")
            LogEntry(schid, subschid, timeText, kgamt)
        }
    }

    private fun fetchDataForDialog(date: String, serialNumber: String, adapterDialog: DialogReportAdapter) {
        viewModelLog.postLog(LogRequest(date))
        viewModelLog.paramResult.observe(viewLifecycleOwner) { paramResult ->
            when (paramResult) {
                is ParamResult.PostLogResponse -> {
                    val logs = paramResult.postLog
                    val logEntries = extractDataFromLogs(logs).map { (schid, subschid, timeText, kgamt) ->
                        LogEntryEntity(schid = schid.toInt(), subschid = subschid.toInt(), time = timeText, serialNum = serialNumber, kgamt = kgamt.toDouble(), date = date)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        for (logEntry in logEntries) {
                            val existingLog = viewModelLog.getLogEntryBySchidAndDate(logEntry.date, logEntry.time)
                            if (existingLog == null) {
                                viewModelLog.insertLogEntry(logEntry)
                            }
                        }
                        val allLogEntries = viewModelLog.getLogEntriesByDateAndSchid(date)
                        withContext(Dispatchers.Main) {
                            adapterDialog.setLogs(allLogEntries)
                            adapterDialog.notifyDataSetChanged()
                        }
                    }
                }
                else -> {
                    // Handle error or other cases if needed
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportFragment()
    }

    override fun onItemClicked(logs: LogResponseX) {
        showDialogReport(logs)
    }
}

