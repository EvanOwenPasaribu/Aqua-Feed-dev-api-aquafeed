package com.aqua_feed.app.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dialylog")
data class LogEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "serial_num") val serialNum: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "schid") val schid: Int,
    @ColumnInfo(name = "subschid") val subschid: Int,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "kgamt") val kgamt: Double
)