package com.aqua_feed.app.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "kg")
    val kg: Double,

    @ColumnInfo(name = "progress")
    val progress: Int,

    @ColumnInfo(name = "status")
    val status: Boolean
)