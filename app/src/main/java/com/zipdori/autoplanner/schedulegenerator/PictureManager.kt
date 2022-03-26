package com.zipdori.autoplanner.schedulegenerator

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zipdori.autoplanner.modules.PermissionModule
import java.text.SimpleDateFormat
import java.util.*

class PictureManager(accessedActivity: AppCompatActivity) : AppCompatActivity() {
    private val guestActivity = accessedActivity

    var selectedImageUri: Uri? = null

    fun openCamera() {
        val NEED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val NEED_PERMISSIONS_FLAGS = arrayOf(
            Flags.FLAG_PERM_CAMERA,
            Flags.FLAG_PERM_STORAGE_FOR_CAMERA,
            Flags.FLAG_PERM_STORAGE_FOR_CAMERA
        )

        // TODO: 2022-03-25 리퀘스트 체크 2가지 다
        // 카메라 및 저장소 권한 체크
        if (PermissionModule.requestPermissionsIfNotExists(NEED_PERMISSIONS, NEED_PERMISSIONS_FLAGS, guestActivity)) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val uri: Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
            selectedImageUri = uri
            // 촬영할 파일 Uri 확인하고 싶을 땐 이 코드 주석
            // Log.d("selectedURI", selectedImageUri.toString())
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
            guestActivity.startActivityForResult(takePictureIntent, Flags.FLAG_REQ_CAMERA)
        }
    }

    fun openGallery() {
        val NEED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val NEED_PERMISSIONS_FLAGS = arrayOf(
            Flags.FLAG_PERM_STORAGE,
            Flags.FLAG_PERM_STORAGE
        )
        if (PermissionModule.requestPermissionsIfNotExists(NEED_PERMISSIONS, NEED_PERMISSIONS_FLAGS, guestActivity)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            guestActivity.startActivityForResult(intent, Flags.GET_GALLERY_IMAGE)
        }
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return guestActivity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }
}