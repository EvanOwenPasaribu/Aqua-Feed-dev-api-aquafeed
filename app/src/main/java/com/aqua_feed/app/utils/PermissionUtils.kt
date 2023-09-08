package com.aqua_feed.app.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun showReadExternalStorage(activity: Activity, onGranted: () -> Unit) {
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED &&
                readExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
            onGranted.invoke()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1)
        }
    }

}