package com.zipdori.autoplanner.schedulegenerator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zipdori.autoplanner.Consts

import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.ActivityListupSchedulecellBinding
import com.zipdori.autoplanner.modules.database.EventExtraInfoVO
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.util.*

class ListupSchedulecellActivity : AppCompatActivity() , View.OnClickListener {

    private lateinit var binding: ActivityListupSchedulecellBinding
    lateinit var scheduleCellAdaptor:ScheduleCellAdapter
    var scheduleList = arrayListOf<EventsVO>()
    var scheduleListExtra = arrayListOf<EventExtraInfoVO>()

    var imgList = ArrayList<Uri>()
    private lateinit var saveIntent: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListupSchedulecellBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("imgURIs")) {
            imgList = intent.getParcelableArrayListExtra("imgURIs")!!
        }
        else
            Log.e("Uri 주소가", "안 넘어옴")

        for(i in imgList){
            Log.e("Uri 확인", i.toString())
        }

        scheduleList = intent.getParcelableArrayListExtra("events")!!
        initRecycler()

        binding.scRegButton.setOnClickListener(this)
        binding.scCancelButton.setOnClickListener(this)
    }

    private fun initRecycler() {
        //뷰홀더의 액티비티 결과 받는 영역
        saveIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    if (result.data != null) {
                        val modifiedEvent:EventsVO? = result.data?.getParcelableExtra("scheduleItem")
                        val modifiedEventExtraVO: EventExtraInfoVO = result.data?.getParcelableExtra("scheduleItemExtra")!!
                        val modifiedEventIdx = modifiedEvent!!.id.toInt() //여기서 이벤트ID를 리스트의 인덱스로 사용중. 나중에 프로바이더에 insert될 때 어차피 새로 정해지는 id
                        scheduleList[modifiedEventIdx] = modifiedEvent
                        scheduleListExtra[modifiedEventIdx] = modifiedEventExtraVO
                        scheduleCellAdaptor.notifyItemChanged(modifiedEventIdx)
                    }
                }
                if(result.resultCode == RESULT_CANCELED) {
                    Log.e("어댑터 호출", "성공")
                }
            }
        scheduleCellAdaptor= ScheduleCellAdapter(this, saveIntent)
        binding.rvScheduleList.adapter = scheduleCellAdaptor

        val now = System.currentTimeMillis()
        val tDate = Date(now)
        val t2Date = Date(now+100000)
        val dFormat = DateForm.integratedForm.format(tDate)
        val d2Format = DateForm.integratedForm.format(t2Date)

        val scheduleListBool = BooleanArray(scheduleList.size) { true }
        scheduleCellAdaptor.scheduleList = scheduleList
        scheduleCellAdaptor.scheduleListExtra = scheduleListExtra
        scheduleCellAdaptor.scheduleListBool = scheduleListBool
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sc_regButton -> {

                //어댑터 안에서 변경되던 리스트 다시 액티비티 관점으로 꺼내서 작업
                val checkedScheduleList = scheduleCellAdaptor.scheduleList
                val checkedScheduleListExtra = scheduleCellAdaptor.scheduleListExtra
                val checkScheduleListBool = scheduleCellAdaptor.scheduleListBool

                //체크되지 않은 리스트 제거
                val len = checkedScheduleList.size

                for (i in len-1 downTo 0){
                    if(!checkScheduleListBool[i]) {
                        checkedScheduleList.removeAt(i)
                        checkedScheduleListExtra.removeAt(i)
                    }
                }

                val intent = Intent()
                setResult(Consts.RESULT_SCHEDULELIST_REG, intent)
                intent.putParcelableArrayListExtra("checkedList", ArrayList(checkedScheduleList))
                intent.putParcelableArrayListExtra("checkedListExtra", ArrayList(checkedScheduleListExtra))
                finish()
            }
            R.id.sc_cancelButton-> {
                finish()
            }
        }
    }
}
