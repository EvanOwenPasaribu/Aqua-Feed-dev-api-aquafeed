package com.aqua_feed.app.utils

import android.content.Context

class PreferencesUtils(appContext: Context) {

    private val sharedPreferences = appContext.getSharedPreferences("aqua_feed", Context.MODE_PRIVATE)

    fun setUserLogin(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

    fun setDate(date: String) {
        val editor = sharedPreferences.edit()
        editor.putString("date", date)
        editor.apply()
    }

    fun setWifi(wifiName: String, wifiIP: String) {
        val editor = sharedPreferences.edit()
        editor.putString("wifiName", wifiName)
        editor.putString("wifiIP", wifiIP)
        editor.apply()
    }

    fun setTime(time: String) {
        val editor = sharedPreferences.edit()
        editor.putString("time", time)
        editor.apply()
    }

    fun getEmail(): String = sharedPreferences.getString("email", "").orEmpty()
    fun getDate(): String = sharedPreferences.getString("date", "").orEmpty()
    fun getWifiIP(): String = sharedPreferences.getString("wifiIP", "192.830.8.9").orEmpty()
    fun getWifiName(): String = sharedPreferences.getString("wifiName", "Mikaelsons").orEmpty()
    fun getTime(): String = sharedPreferences.getString("time", "").orEmpty()

    fun reset() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("date")
        editor.remove("time")
        editor.apply()
    }

    fun setNextDaySelected(isSelected: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("nextDaySelected", isSelected)
        editor.apply()
    }

    fun isNextDaySelected(): Boolean = sharedPreferences.getBoolean("nextDaySelected", false)
}