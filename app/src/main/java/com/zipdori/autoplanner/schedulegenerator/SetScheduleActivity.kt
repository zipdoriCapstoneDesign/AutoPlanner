package com.zipdori.autoplanner.schedulegenerator

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.zipdori.autoplanner.databinding.ActivitySetScheduleBinding
import com.google.android.material.snackbar.Snackbar
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener

import androidx.core.content.ContextCompat
//import android.R

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.zipdori.autoplanner.R
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class SetScheduleActivity : AppCompatActivity(),View.OnClickListener  {
    private val binding by lazy{ActivitySetScheduleBinding.inflate(layoutInflater)}

    //컬러피커 관련 변수. null 관련 관리 편의상 적당히 초기화  --------------승화
    var pickedColor:String = "#5EAEFF"
    var coloredBtnStartColor:Int = -10572033

    //처음 컬러피커 버튼 색상. 이것도 적당히 초기화
    var coloredBtnColor:Int = coloredBtnStartColor

    val pManager:PictureManager = PictureManager(this)
    var selectedImageUri: Uri? = null

    //같은 레이아웃을 쓰는 기간 설정 버튼 기간 시작일인지 종료일인지 나누는 플래그
    val FLAG_FROM = 500
    val FLAG_TO = 501

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val view = binding.root
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(view)

        initTextForButton()

        //---------------------버튼---------------------

        //색상 버튼 눌렀을 때 -----
        binding.coloredNormalButton.setOnClickListener() { openColorPickerDialog(view) }
        binding.fromDateBtn.setOnClickListener(this)
        binding.toDateBtn.setOnClickListener(this)
        binding.fromTimeBtn.setOnClickListener(this)
        binding.toTimeBtn.setOnClickListener(this)

        //사진 레이아웃 눌렀을 때
        binding.uploadImage.setOnClickListener(this)
        binding.uploadImageFromGallery.setOnClickListener(this)

        binding.uploadedImage.setOnClickListener(this)

        // 등록/취소 버튼들
        binding.backButton.setOnClickListener(){
            finish()
        }
        binding.regButton.setOnClickListener(){
            binding.regButton.text = "눌림"
        }
    }

    // OnClick 코드들
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(p0: View?) {
        when(p0?.id) {
            // 기간 시작일 설정
            R.id.fromDateBtn -> {
                setInfoForDateButton(FLAG_FROM)
            }
            R.id.fromTimeBtn->{
                setInfoForTimeButton(FLAG_FROM)
            }
            R.id.toDateBtn -> {
                setInfoForDateButton(FLAG_TO)
            }
            R.id.toTimeBtn->{
                setInfoForTimeButton(FLAG_TO)
            }
            R.id.uploadImage -> {
                pManager.openCamera()
            }
            R.id.uploadImageFromGallery -> {
                pManager.openGallery()
            }
            R.id.uploadedImage ->{
                val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
                val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.schedulepicture_expanded, null) as ConstraintLayout

                val expandedImage:ImageView = constraintLayout.findViewById(R.id.imageExpanded)
                val expandedClose:Button = constraintLayout.findViewById(R.id.imgExpand_return)

                expandedImage.setImageURI(selectedImageUri)
                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                builder.setView(constraintLayout)
                val alertDialog: android.app.AlertDialog = builder.create()
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                alertDialog.show()

                expandedClose.setOnClickListener {
                    alertDialog.dismiss()
                }
            }


        }
    }

    private fun setInfoForTimeButton(fromToFlag: Int) {
        val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
        val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.dialog_set_schedule_time, null) as ConstraintLayout

        val regBtn:Button = constraintLayout.findViewById(R.id.setTimeRegButton)
        val exitBtn:Button = constraintLayout.findViewById(R.id.setTimeBackButton)

        val ampm:NumberPicker= constraintLayout.findViewById(R.id.set_ampm)
        val hour:NumberPicker= constraintLayout.findViewById(R.id.num_picker_hour)
        val minute:NumberPicker= constraintLayout.findViewById(R.id.num_picker_minute)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(constraintLayout)
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        alertDialog.show()

        ampm.wrapSelectorWheel = false

        hour.minValue=1
        hour.maxValue=12

        minute.minValue=0
        minute.maxValue=59

        ampm.minValue=0
        ampm.maxValue=1
        ampm.displayedValues = arrayOf("오전","오후")

        regBtn.setOnClickListener{
            var minTxt:String = if(minute.value<10) {"0"+minute.value.toString()} else {minute.value.toString()}
            var ampmTxt:String = if(ampm.value==0){"오전 "} else {"오후 "}

            if(fromToFlag == FLAG_FROM) binding.fromTimeBtn.text =ampmTxt + hour.value.toString()+":"+minTxt
            else binding.toTimeBtn.text =ampmTxt + hour.value.toString()+":"+minTxt
            alertDialog.dismiss()
        }
        exitBtn.setOnClickListener{
            alertDialog.dismiss()
        }
    }

    private fun setInfoForDateButton(fromToFlag: Int) {
        val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
        val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.dialog_set_schedule_date, null) as ConstraintLayout

        val regBtn:Button = constraintLayout.findViewById(R.id.calRegButton)
        val exitBtn:Button = constraintLayout.findViewById(R.id.calBackButton)
        val calForSet:CalendarView = constraintLayout.findViewById(R.id.calForSetTerm)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(constraintLayout)
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        alertDialog.show()

        // 기간설정 버튼에 적힌 날짜가 불러와져야 되는데 당분간 오늘날짜로 무조건 뜨게 놔두고 나중에 데이터베이스 정리가 되면 조정할 붖분
        var selectedYear:Int = 2022
        var selectedMonth:Int = intent.getStringExtra("FromDateMonth")!!.toInt()
        var selectedDay:Int = intent.getStringExtra("FromDateDay")!!.toInt()

        // 캘린더에서 날짜 클릭할 때마다 호출되는 함수
        calForSet.setOnDateChangeListener { calendarView, year, month, day ->
            selectedYear=year
            selectedMonth=month
            selectedDay=day
            Toast.makeText(this,month.toString()+"."+day.toString(),Toast.LENGTH_SHORT).show()
        }

        // 기간설정 캘린더 다이얼로그의 등록/취소 버튼
        regBtn.setOnClickListener {
            var selectedMonthStr:String
            var selectedDayStr:String

            //03월이 3월로 뜨는 식의 현상 교정 (03으로 통일)
            if (selectedMonth < 10) selectedMonthStr = "0$selectedMonth"
            else selectedMonthStr = "$selectedMonth"

            if (selectedDay < 10) selectedDayStr = "0$selectedDay"
            else selectedDayStr = "$selectedDay"

            val resultTxt = selectedMonthStr + "월 " + selectedDayStr + "일"
            if (fromToFlag == FLAG_FROM) binding.fromDateBtn.text = resultTxt
            else binding.toDateBtn.text = resultTxt

            alertDialog.dismiss()
        }
        exitBtn.setOnClickListener{
            alertDialog.dismiss()
        }

    }

    //컬러피커 다이얼로그 (라이브러리를 통해 동적으로 레이아웃 만들어서 열리는걸로 보임)
    private fun openColorPickerDialog(view: ScrollView) {
        //컬러피커버튼 색 바꾸기 위한 변수들 ------------
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_colorpickerbutton) as GradientDrawable?
        val ivShape: ImageView = binding.coloredNormalButton

        val colorPicker = ColorPicker(this@SetScheduleActivity)
        val colors: ArrayList<String> = ArrayList()

        colors.add("#5EAEFF")
        colors.add("#82B926")
        colors.add("#a276eb")
        colors.add("#6a3ab2")
        colors.add("#666666")
        colors.add("#FFFF00")
        colors.add("#3C8D2F")
        colors.add("#FA9F00")
        colors.add("#FF0000")


        colorPicker
            .setDefaultColorButton(Color.parseColor("#f84c44"))
            .setColors(colors)
            .setColumns(5)
            .setRoundColorButton(true)
            .setOnChooseColorListener(object : OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    Log.d(
                        "position",
                        "" + position
                    ) // will be fired only when OK button was tapped
                    coloredBtnColor = color

                    //색상 받는 곳은 여기
                    pickedColor = Integer.toHexString(color)

                    Snackbar.make(view, pickedColor, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

                    //컬러피커 버튼 색 변경
                    drawable?.setColor(color);
                    ivShape.setImageDrawable(drawable);
                }

                override fun onCancel() {}
            })
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initTextForButton() {
        //현재 날짜,시간 받아와서 기간 설정 버튼들에 텍스트로 설정(어떻게 이 액티비티에 접근하는지(날짜칸/fab 등) 에 따라 바껴야하는 부분)
        var now = LocalDateTime.now()

        var strnow = intent.getStringExtra("FromDateMonth") + "월 "+ intent.getStringExtra("FromDateDay") +"일"
        binding.fromDateBtn.text = strnow
        strnow = intent.getStringExtra("ToDateMonth") + "월 "+ intent.getStringExtra("ToDateDay") +"일"
        binding.toDateBtn.text = strnow

        var hourNow = intent.getStringExtra("FromTime")!!.toInt()
        var isPM:Boolean = hourNow>=12

        var strTimeNow = "??:00"
        if(isPM){
            if(hourNow>12) hourNow-=12
            strTimeNow = "오후 $hourNow:00"
        }
        else{
            strTimeNow = "오전 $hourNow:00"
        }
        binding.fromTimeBtn.text = strTimeNow

        hourNow = intent.getStringExtra("ToTime")!!.toInt()
        isPM = hourNow>=12

        strTimeNow = "??:00"
        if(isPM){
            if(hourNow>12) hourNow-=12
            strTimeNow = "오후 $hourNow:00"
        }
        else{
            strTimeNow = "오전 $hourNow:00"
        }
        binding.toTimeBtn.text = strTimeNow
    }

    //startActivityForResult 을 사용한 다음 돌아오는 결과값을 해당 메소드로 호출합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                Flags.FLAG_REQ_CAMERA -> {
                    selectedImageUri = pManager.selectedImageUri
                    binding.uploadedImage.setImageURI(selectedImageUri)
                }

                Flags.GET_GALLERY_IMAGE -> {
                    if(data != null && data.data != null){
                        selectedImageUri = data.data
                        binding.uploadedImage.setImageURI(selectedImageUri)
                        //갤러리 사진 링크 확인시
                        //Log.e("selectedURI", selectedImageUri.toString())
                    }

                }
            }
        }
    }
    //checkPermission() 에서 ActivityCompat.requestPermissions 을 호출한 다음 사용자가 권한 허용여부를 선택하면 해당 메소드로 값이 전달 됩니다.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            Flags.FLAG_PERM_STORAGE_FOR_CAMERA ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(this,"저장소 권한을 승인해야지만 앱을 사용할 수 있습니다. 앱 정보에서 승인해주세요.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                //카메라 호출 메소드
                pManager.openCamera()
            }
            Flags.FLAG_PERM_CAMERA ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다. 앱 정보에서 승인해주세요.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                pManager.openCamera()
            }
            Flags.FLAG_PERM_STORAGE ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(this,"저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                pManager.openGallery()
            }
        }
    }

}