package com.zipdori.autoplanner.schedulegenerator

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zipdori.autoplanner.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BuiltInCameraActivity(accessedActivity: AppCompatActivity) : AppCompatActivity() {
    private val guestActivity = accessedActivity
    //Manifest 에서 설정한 카메라 권한을 가지고 온다.
    private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    private val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var camRunning = false
    //권한 플래그값 정의
    private val FLAG_PERM_CAMERA = 98
    private val FLAG_PERM_STORAGE = 99

    //카메라와 갤러리를 호출하는 플래그
    private val FLAG_REQ_CAMERA = 101
    private val GET_GALLERY_IMAGE = 200
//    private lateinit var currentPhotoPath: String

    var selectedImageUri: Uri? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    fun openCamera() {
        //카메라 권한이 있는지 확인
        if(checkPermission(CAMERA_PERMISSION,FLAG_PERM_CAMERA)){
            camRunning=true
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            // val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES
            val uri : Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
            selectedImageUri = uri
            Log.d("selectedURI",selectedImageUri.toString())
            Log.e("코드도달","내가먼전데")
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
//            guestActivity.startActivityForResult(takePictureIntent,FLAG_REQ_CAMERA)

        }
    }

    fun createImageUri(filename:String, mimeType:String): Uri?{
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return guestActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }


//    // 사진 파일을 만드는 메소드
//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//            Log.d("test", "currentPhotoPath : $currentPhotoPath")
//        }
//    }
//
//    // 갤러리에 파일을 추가하는 함수.
//    private fun galleryAddPic() {
//        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
//            Log.d("test", "currentPhotoPath2 : $currentPhotoPath")
//            val f = File(currentPhotoPath)
//            mediaScanIntent.data = Uri.fromFile(f)
//            sendBroadcast(mediaScanIntent)
//        }
//    }

    //권한이 있는지 체크하는 메소드
    fun checkPermission(permissions:Array<out String>,flag:Int):Boolean{
        //안드로이드 버전이 마쉬멜로우 이상일때
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(permission in permissions){
                //만약 권한이 승인되어 있지 않다면 권한승인 요청을 사용에 화면에 호출합니다.
                if(ContextCompat.checkSelfPermission(guestActivity,permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(guestActivity,permissions,flag)
                    return false
                }
            }
        }
        return true
    }

    //checkPermission() 에서 ActivityCompat.requestPermissions 을 호출한 다음 사용자가 권한 허용여부를 선택하면 해당 메소드로 값이 전달 됩니다.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            FLAG_PERM_STORAGE ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(guestActivity,"저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                //카메라 호출 메소드
                openCamera()
            }
            FLAG_PERM_CAMERA ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(guestActivity,"카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                openCamera()
            }
        }
    }

    //startActivityForResult 을 사용한 다음 돌아오는 결과값을 해당 메소드로 호출합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            //selectedImageUri = data.data
            //val TAG = "MyActivity"
            //Log.d(TAG,selectedImageUri.toString())
        }
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    if( selectedImageUri != null) {
                       camRunning=false
                    }

                }
            }
        }
        //갤러리
        if (requestCode == GET_GALLERY_IMAGE && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            //binding.uploadedImage.setImageURI(selectedImageUri)

        }
    }

    fun loadBitmapFromMediaStoreBy(photoUri: Uri) : Bitmap?{
        var image: Bitmap? = null
        try{
            image = if(Build.VERSION.SDK_INT > 27){
                val source: ImageDecoder.Source = ImageDecoder.createSource(guestActivity.contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            }else{
                MediaStore.Images.Media.getBitmap(guestActivity.contentResolver, photoUri)
            }
        }catch(e: IOException){
            e.printStackTrace()
        }
        return image
    }

}