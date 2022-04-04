package com.zipdori.autoplanner.schedulegenerator

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.ActivitySetScheduleBinding
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.schedulegenerator.DateForm.Companion.calMdForm
import com.zipdori.autoplanner.schedulegenerator.DateForm.Companion.calhmForm
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SetScheduleActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivitySetScheduleBinding.inflate(layoutInflater) }

    //컬러피커 관련 변수. null 관련 관리 편의상 적당히 초기화  --------------승화
    var pickedColor: String = "#5EAEFF"
    var coloredBtnStartColor: Int = -10572033

    //처음 컬러피커 버튼 색상. 이것도 적당히 초기화
    var coloredBtnColor: Int = coloredBtnStartColor
    var selectedImageUri: Uri? = null

    //같은 레이아웃을 쓰는 기간 설정 버튼 기간 시작일인지 종료일인지 나누는 플래그
    val FLAG_FROM = 500
    val FLAG_TO = 501

    //기간설정 시간순이 뒤집혔을 때 재설정 범위
    val FLAG_DAY = 600
    val FLAG_HOUR = 601

    var planFrom: Calendar = Calendar.getInstance()
    var planTo: Calendar = Calendar.getInstance()
    var tempCal:Calendar? = null // 연월일 설정시 캘린더에서 날짜를 눌러대면 등록 버튼 누를 때까지 바로 적용되지 않게 임시 캘린더 변수



    ////로그 확인 시 참고 형태 Log.e("btemp", calCheckForm.format(tempCal!!.time))
    val calCheckForm = SimpleDateFormat("yy.MM.dd hh:mm")

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = binding.root
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(view)

        // 액티비티가 처음 틀어질 때 기간 설정 버튼에 입력되어있을 시작/종료일.시간 설정
        planFrom.timeInMillis = intent.getLongExtra("FromDate",0)
        planTo.timeInMillis = intent.getLongExtra("ToDate",0)

        writeDateTimeToButton()
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
        binding.backButton.setOnClickListener() {
            finish()
        }
        binding.regButton.setOnClickListener(this)
    }

    // OnClick 코드들
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            // 기간 시작일 설정
            R.id.fromDateBtn -> {
                setInfoByDateButton(FLAG_FROM)
            }
            R.id.fromTimeBtn -> {
                setInfoByTimeButton(FLAG_FROM)
            }
            R.id.toDateBtn -> {
                setInfoByDateButton(FLAG_TO)
            }
            R.id.toTimeBtn -> {
                setInfoByTimeButton(FLAG_TO)
            }
            R.id.uploadImage -> {
                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(NEED_PERMISSIONS,Consts.FLAG_PERM_CAMERA)
            }
            R.id.uploadImageFromGallery -> {
                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(NEED_PERMISSIONS,Consts.FLAG_PERM_STORAGE)
            }
            // TODO: 2022-03-27 이미지뷰에 채운 사진 없애는 기능
            R.id.uploadedImage -> {
                val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
                val constraintLayout: ConstraintLayout = layoutInflater.inflate(
                    R.layout.schedulepicture_expanded,
                    null
                ) as ConstraintLayout

                val expandedImage: ImageView = constraintLayout.findViewById(R.id.imageExpanded)
                val expandedClose: Button = constraintLayout.findViewById(R.id.imgExpand_return)

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
            R.id.regButton -> {
                val calendarId = 1
                val title = binding.tietScheduleTitle.text.toString()
                val description = binding.etScheduleDescription
                val dtStart = planFrom.timeInMillis
                val dtEnd = planTo.timeInMillis
                val eventTimeZone = "UTC"

                val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(applicationContext)
                calendarProviderModule.insertEvent(calendarId, title, null, description.text.toString(), coloredBtnColor, dtStart, dtEnd, eventTimeZone, null, null, null, null)

                finish()
                // TODO: 2022-04-05 등록 시 HomeFragment에 바로 적용이 되어 등록된 일정이 보이도록 설정
            }
        }
    }


    // TODO: 2022-03-27 이하 3개 함수 리팩토링의 여지가 있을 것 같습니다. 다른 레이아웃 만드는 거보다 이런거 해놓고 넘어가는게 좋다던가 작업 우선순위로 두는게 좋다면 알려주세요
    // TODO: planFrom과 planTo가 완전히는 아니지만 제법 데칼코마니 구성
    private fun setInfoByDateButton(fromToFlag: Int) {
        // 기간 날짜 지정하는 레이아웃 불러오기
        val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
        val constraintLayout: ConstraintLayout =
            layoutInflater.inflate(R.layout.dialog_set_schedule_date, null) as ConstraintLayout

        val regBtn: Button = constraintLayout.findViewById(R.id.calRegButton)
        val exitBtn: Button = constraintLayout.findViewById(R.id.calBackButton)
        val calForSet: CalendarView = constraintLayout.findViewById(R.id.calForSetTerm)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(constraintLayout)
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        alertDialog.show()

        if(fromToFlag == FLAG_FROM) {
            tempCal = planFrom.clone() as Calendar
            calForSet.setDate(planFrom.timeInMillis)
        }else {
            tempCal = planTo.clone() as Calendar
            calForSet.setDate(planTo.timeInMillis)
        }
        // 캘린더에서 날짜 클릭할 때마다 호출되는 함수
        calForSet.setOnDateChangeListener { calendarView, year, month, day ->
            tempCal!!.set(year, month, day)
            Toast.makeText(this, month.toString() + "." + day.toString(), Toast.LENGTH_SHORT).show()
        }

        // 연월일 설정하는 버튼으로 연 캘린더 다이얼로그의 등록/취소 버튼
        regBtn.setOnClickListener {

            // 버튼에 해당하는 시간은 반드시 지정한 시간이 됨
            if (fromToFlag == FLAG_FROM)
                planFrom = tempCal!!.clone() as Calendar

            else
                planTo = tempCal!!.clone() as Calendar

            //기간 시작일과 종료일의 시간순이 뒤집혔을 때
            if (planFrom.timeInMillis >= planTo.timeInMillis) {
                timeUntieBy(fromToFlag, FLAG_DAY)
            }

            //기간 시작종료일 셋팅이 끝났으니 텍스트 리프레시
            writeDateTimeToButton()
            alertDialog.dismiss()
        }
        exitBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun setInfoByTimeButton(fromToFlag: Int) {
        val layoutInflater: LayoutInflater = (this as FragmentActivity).layoutInflater
        val constraintLayout: ConstraintLayout =
            layoutInflater.inflate(R.layout.dialog_set_schedule_time, null) as ConstraintLayout

        val regBtn: Button = constraintLayout.findViewById(R.id.setTimeRegButton)
        val exitBtn: Button = constraintLayout.findViewById(R.id.setTimeBackButton)

        val ampm: NumberPicker = constraintLayout.findViewById(R.id.set_ampm)
        val hour: NumberPicker = constraintLayout.findViewById(R.id.num_picker_hour)
        val minute: NumberPicker = constraintLayout.findViewById(R.id.num_picker_minute)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(constraintLayout)
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        alertDialog.show()

        ampm.wrapSelectorWheel = false

        hour.minValue = 1
        hour.maxValue = 12

        minute.minValue = 0
        minute.maxValue = 59

        ampm.minValue = 0
        ampm.maxValue = 1
        ampm.displayedValues = arrayOf("오전", "오후")

        if(fromToFlag == FLAG_FROM) {
            if (planFrom.get(Calendar.HOUR_OF_DAY) < 12) ampm.value = 0
            else ampm.value = 1
            hour.value = planFrom.get(Calendar.HOUR)
            minute.value = planFrom.get(Calendar.MINUTE)
        }
        else {
            if (planTo.get(Calendar.HOUR_OF_DAY) < 12) ampm.value = 0
            else ampm.value = 1
            hour.value = planTo.get(Calendar.HOUR)
            minute.value = planTo.get(Calendar.MINUTE)
        }

        if(fromToFlag == FLAG_FROM)
            tempCal = planFrom.clone() as Calendar
        else
            tempCal = planTo.clone() as Calendar

        regBtn.setOnClickListener {
            //오전 11시는 값이 11이지만 오전 12시는 값이 0이다. 오후도 비슷한 맥락에서 조정
            var hourBound = hour.value
            if (hourBound == 12) hourBound -=12
            // 오전오후 값을 보고 넘버피커에서 오후 1시로 보이면 13시로 셋팅하는 방식
            if (ampm.value == 0)
                tempCal!!.set(Calendar.HOUR_OF_DAY, hourBound)
            else
                tempCal!!.set(Calendar.HOUR_OF_DAY, hourBound+12)

            tempCal!!.set(Calendar.MINUTE, minute.value)

            //  버튼에 해당하는 시간은 반드시 지정한 시간이 됨
            if (fromToFlag == FLAG_FROM)
                planFrom = tempCal!!.clone() as Calendar
            else
                planTo = tempCal!!.clone() as Calendar

            if (planFrom.timeInMillis >= planTo.timeInMillis) {
                timeUntieBy(fromToFlag, FLAG_HOUR)
            }
            writeDateTimeToButton()
            alertDialog.dismiss()

        }
        exitBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    //기간설정하다가 시간순이 뒤집혔을 때 풀어주는 함수
    
    private fun timeUntieBy(fromToFlag:Int, timeUnit: Int) {
        // 기간 시작일 버튼으로 들어왔다면
        if (fromToFlag == FLAG_FROM) {
            //기간 종료일을 시작일과 맞춘다
            planTo.set(Calendar.YEAR, planFrom.get(Calendar.YEAR))
            planTo.set(Calendar.MONTH, planFrom.get(Calendar.MONTH))
            planTo.set(Calendar.DAY_OF_MONTH, planFrom.get(Calendar.DAY_OF_MONTH))

            //연월일을 맞췄는데 시.분 때문에 여전히 뒤집힌 상태라면
            if (planFrom.timeInMillis >= planTo.timeInMillis) {
                if(timeUnit==FLAG_DAY)
                    planTo.add(Calendar.DAY_OF_MONTH, 1) //[연월일 설정] 버튼으로 들어왔다면 마감일에 하루 연장
                else{                                                       //[시.분 설정] 버튼으로 들어왔다면
                    planTo.set(Calendar.HOUR_OF_DAY, planFrom.get(Calendar.HOUR_OF_DAY))    //마감일 연장 안 하고 시간까지 일치시켜보기
                    if (planFrom.timeInMillis >= planTo.timeInMillis) planTo.add(Calendar.HOUR, 1)   //그래도 분 때문에 시간 역전이 안 되면 마감시간에 한 시간 연장

                }
            }
        } else {
            planFrom.set(Calendar.YEAR, planTo.get(Calendar.YEAR))
            planFrom.set(Calendar.MONTH, planTo.get(Calendar.MONTH))
            planFrom.set(Calendar.DAY_OF_MONTH, planTo.get(Calendar.DAY_OF_MONTH))
            //연월일을 맞췄음에도 여전히 시간순이 뒤집혀있으면
            if (planFrom.timeInMillis >= planTo.timeInMillis) {
                //시작일 하루 앞으로
                if(timeUnit==FLAG_DAY)
                    planFrom.add(Calendar.DAY_OF_MONTH, -1)
                else {
                    planFrom.set(Calendar.HOUR_OF_DAY, planTo.get(Calendar.HOUR_OF_DAY))    //시간까지 일치시켜보기
                    if (planFrom.timeInMillis >= planTo.timeInMillis) planFrom.add(Calendar.HOUR, -1)   //그래도 분 때문에 시간 역전이 안 되면 시작시간 한 시간 감소
                }
            }
        }

    }

    private fun writeDateTimeToButton() {
        //기간 설정 4개 버튼 텍스트 리프레시
        binding.fromDateBtn.text = calMdForm.format(planFrom.time)
        binding.fromTimeBtn.text = calhmForm.format(planFrom.time)

        binding.toDateBtn.text = calMdForm.format(planTo.time)
        binding.toTimeBtn.text = calhmForm.format(planTo.time)
    }

    //컬러피커 다이얼로그 (라이브러리를 통해 동적으로 레이아웃 만들어서 열리는걸로 보임)
    private fun openColorPickerDialog(view: ScrollView) {
        //컬러피커버튼 색 바꾸기 위한 변수들 ------------
        val drawable = ContextCompat.getDrawable(
            this,
            R.drawable.ic_colorpickerbutton
        ) as GradientDrawable?
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

    //startActivityForResult 을 사용한 다음 돌아오는 결과값을 해당 메소드로 호출합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Consts.FLAG_REQ_CAMERA -> {
                    binding.uploadedImage.setImageURI(selectedImageUri)
                }

                Consts.GET_GALLERY_IMAGE -> {
                    if (data != null && data.data != null) {
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            Consts.FLAG_PERM_CAMERA -> {
                Log.e("승인절차","카메라")
                for (grant in grantResults) {
                    Log.e("승인절차",grant.toString())
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            this,
                            "카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다. 앱 정보에서 승인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                Log.e("승인절차","카메라 승인 확인")
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val uri: Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
                selectedImageUri = uri

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
                startActivityForResult(takePictureIntent, Consts.FLAG_REQ_CAMERA)
            }
            Consts.FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(
                            this,
                            "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, Consts.GET_GALLERY_IMAGE)
            }
        }
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }
}