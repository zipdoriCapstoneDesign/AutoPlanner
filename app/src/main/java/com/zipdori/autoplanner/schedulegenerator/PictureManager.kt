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
import java.text.SimpleDateFormat
import java.util.*

class PictureManager(accessedActivity: AppCompatActivity) : AppCompatActivity() {
    private val guestActivity = accessedActivity

    //Manifest 에서 설정한 카메라 권한을 가지고 온다.
    private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    private val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var selectedImageUri: Uri? = null


    fun openCamera() {
        // TODO: 2022-03-25 리퀘스트 체크 2가지 다
        //저장소권한 확인
        if (checkPermission(STORAGE_PERMISSION, Flags.FLAG_PERM_STORAGE_FOR_CAMERA)) {
            //카메라 권한이 있는지 확인
            if (checkPermission(CAMERA_PERMISSION, Flags.FLAG_PERM_CAMERA)) {
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
    }

    fun openGallery() {
        if (checkPermission(STORAGE_PERMISSION, Flags.FLAG_PERM_STORAGE)){
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

    //권한이 있는지 체크하는 메소드
    private fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        //안드로이드 버전이 마쉬멜로우 이상일때
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                //만약 권한이 승인되어 있지 않다면 권한승인 요청을 사용에 화면에 호출합니다.
                if (ContextCompat.checkSelfPermission(
                        guestActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(guestActivity, permissions, flag)
                    return false
                }
            }
        }
        return true
    }


}