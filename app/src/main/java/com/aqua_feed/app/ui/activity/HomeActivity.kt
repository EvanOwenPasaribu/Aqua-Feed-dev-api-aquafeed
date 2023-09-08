package com.aqua_feed.app.ui.activity

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.ActivityHomeBinding
import com.aqua_feed.app.ui.fragment.CalculatorFragment
import com.aqua_feed.app.ui.fragment.CalibrationFragment
import com.aqua_feed.app.ui.fragment.DeviceInfoFragment
import com.aqua_feed.app.ui.fragment.HomeFragment
import com.aqua_feed.app.ui.fragment.ReportFragment
import com.aqua_feed.app.ui.fragment.RunTestFragment
import com.aqua_feed.app.ui.fragment.ScheduleFragment
import com.aqua_feed.app.ui.fragment.SettingsFragment
import com.aqua_feed.app.utils.PreferencesUtils
import com.aqua_feed.app.utils.Utils
import com.aquafeed.app.view.fragment.SensorFragment
import com.aqua_feed.app.ui.fragment.WeatherFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var preferencesUtils: PreferencesUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        preferencesUtils = PreferencesUtils(this)
        openFragment(HomeFragment.newInstance())
        setupBottomNav()
        setupSideNav()

        setupTime()
        setupDate()
        binding.btnSettings.setOnClickListener {
            binding.txtTitle.text = "Settings"
            openFragment(SettingsFragment.newInstance())
        }

        val animation1 = TranslateAnimation(0.0f, 1500.0f, 0.0f, 0.0f)
        animation1.duration = 500
        animation1.fillAfter = true
        binding.btnSideMenu.setOnClickListener {
            binding.sideMenu.layoutSideMenu.visibility = View.VISIBLE
            //binding.sideMenu.layoutSideMenu.startAnimation(animation1)
        }
        binding.sideMenu.btnHideSideMenu.setOnClickListener {
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
    }

    private fun setupSideNav() {
        binding.sideMenu.btnClose.setOnClickListener {
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnSchedule.setOnClickListener {
            binding.txtTitle.text = "Schedule"
            openFragment(ScheduleFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnCalibration.setOnClickListener {
            setMenuColor(binding.txtCalibration, binding.iconCalibration)
            binding.txtTitle.text = "Calibration"
            openFragment(CalibrationFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnRunTest.setOnClickListener {
            setMenuColor(binding.txtRunTest, binding.iconRunTest)
            binding.txtTitle.text = "Run Test"
            openFragment(RunTestFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnReport.setOnClickListener {
            setMenuColor(binding.txtReport, binding.iconReport)
            binding.txtTitle.text = "Report"
            openFragment(ReportFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnWeather.setOnClickListener {
            binding.txtTitle.text = "Weather"
            openFragment(WeatherFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnBatteryCharging.setOnClickListener {
            binding.txtTitle.text = "Battery Charging"
            openFragment(WeatherFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnCalculator.setOnClickListener {
            binding.txtTitle.text = "Calculator"
            openFragment(CalculatorFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnSettingsSide.setOnClickListener {
            binding.txtTitle.text = "Settings"
            openFragment(SettingsFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnSensor.setOnClickListener {
            binding.txtTitle.text = "Sensor"
            openFragment(SensorFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnDeviceInfo.setOnClickListener {
            binding.txtTitle.text = "Device Info"
            openFragment(DeviceInfoFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        val selectDeviceDialog = Utils.showDialogSelectDevice(this) {

        }
        binding.sideMenu.btnSelectDevice.setOnClickListener {
            selectDeviceDialog.show()
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        binding.sideMenu.btnLogout.setOnClickListener {
            preferencesUtils.reset()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    private fun setupDate() {
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy", Locale.US)
        val dateCalendar = Calendar.getInstance()
        val datePicker = Utils.showDatePicker(this)

        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            dateCalendar.set(Calendar.YEAR, year)
            dateCalendar.set(Calendar.MONTH, month)
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateValue = dateFormat.format(dateCalendar.time)
            preferencesUtils.setDate(dateValue)

            binding.txtTitle.text = "AquaFeed"
            openFragment(HomeFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }

        binding.sideMenu.btnUpdateDate.setOnClickListener {
            datePicker.show()
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
    }

    private fun setupTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val timeCalendar = Calendar.getInstance()
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            timeCalendar.set(Calendar.MINUTE, minute)

            val timeValue = timeFormat.format(timeCalendar.time)
            preferencesUtils.setTime(timeValue)

            binding.txtTitle.text = "AquaFeed"
            openFragment(HomeFragment.newInstance())
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
        val timePicker = Utils.showTimePicker(this, timePickerListener)

        binding.sideMenu.btnUpdateTime.setOnClickListener {
            timePicker.show()
            binding.sideMenu.layoutSideMenu.visibility = View.GONE
        }
    }

    private fun setMenuColor(text: TextView, image: ImageView) {
        binding.txtHome.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        binding.txtCalibration.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        binding.txtRunTest.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        binding.txtReport.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        text.setTextColor(ContextCompat.getColor(this, R.color.white_A702))

        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.gray_500), android.graphics.PorterDuff.Mode.MULTIPLY)
        binding.iconReport.setColorFilter(ContextCompat.getColor(this, R.color.gray_500), android.graphics.PorterDuff.Mode.MULTIPLY)
        binding.iconRunTest.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_playproperty))
        binding.iconCalibration.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_height))

        when (image.id) {
            R.id.iconCalibration -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_height_white))
            }
            R.id.iconRunTest -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_playproperty_white))
            }
            else -> {
                image.setColorFilter(ContextCompat.getColor(this, R.color.white_A702), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.container.id, fragment)
            commit()
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            setMenuColor(binding.txtHome, binding.iconHome)

            binding.txtTitle.text = getString(R.string.app_name)
            openFragment(HomeFragment.newInstance())
        }
        binding.navCalibration.setOnClickListener {
            setMenuColor(binding.txtCalibration, binding.iconCalibration)

            binding.txtTitle.text = "Calibration"
            openFragment(CalibrationFragment.newInstance())
        }
        binding.navRunTest.setOnClickListener {
            setMenuColor(binding.txtRunTest, binding.iconRunTest)

            binding.txtTitle.text = "Run Test"
            openFragment(RunTestFragment.newInstance())
        }
        binding.navReport.setOnClickListener {
            setMenuColor(binding.txtReport, binding.iconReport)

            binding.txtTitle.text = "Report"
            openFragment(ReportFragment.newInstance())
        }
    }
}