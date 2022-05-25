package com.zipdori.autoplanner.modules.common

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

class PermissionModule {
    companion object {
        fun requestPermissionsIfNotExists(permissions: Array<out String>, flagArray: Array<Int>, activity: Activity): Boolean {
            for (i in 0 until flagArray.count()) {
                Log.e("Flagarray",flagArray.get(i).toString())
                if (ContextCompat.checkSelfPermission(activity, permissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        activity,
                        permissions,
                        flagArray.get(i)
                    )
                     return false
                }
            }
            return true
        }
    }
}