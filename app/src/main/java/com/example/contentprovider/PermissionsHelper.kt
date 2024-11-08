package com.example.contentprovider

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: MainActivity, permission: String, requestCode: Int) {
        if (!hasPermission(activity, permission)) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }
}
