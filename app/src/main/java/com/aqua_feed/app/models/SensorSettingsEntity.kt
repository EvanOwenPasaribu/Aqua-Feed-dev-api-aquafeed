package com.aqua_feed.app.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_settings")
data class SensorSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "is_battery_active")
    val isBatteryActive: Boolean,

    @ColumnInfo(name = "is_solar_active_active")
    val isSolarPanelActive: Boolean,

    @ColumnInfo(name = "is_water_temperature_active")
    val isWaterTemperatureActive: Boolean,

    @ColumnInfo(name = "is_water_ph_active")
    val isWaterPHActive: Boolean,

    @ColumnInfo(name = "is_salinity_active")
    val isSalinityActive: Boolean,

    @ColumnInfo(name = "is_turbidity_active")
    val isTurbidityActive: Boolean,
)