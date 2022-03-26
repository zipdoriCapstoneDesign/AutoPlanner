package com.zipdori.autoplanner.modules

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionModule {
    companion object {
        fun requestPermissionsIfNotExists(permissions: Array<out String>, flagArray: Array<Int>, appCompatActivity: AppCompatActivity): Boolean {
            for (i in 0 until flagArray.count()) {
                if (ContextCompat.checkSelfPermission(appCompatActivity, permissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        appCompatActivity,
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