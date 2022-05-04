package com.zipdori.autoplanner.schedulegenerator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zipdori.autoplanner.Consts

import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.ActivityListupSchedulecellBinding
import com.zipdori.autoplanner.modules.calendarprovider.EventExtraInfo
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.ui.home.HomeFragment
import java.util.*

class ListupSchedulecellActivity : AppCompatActivity() , View.OnClickListener {

    private lateinit var binding: ActivityListupSchedulecellBinding
    lateinit var scheduleCellAdaptor:ScheduleCellAdapter
    val scheduleList = mutableListOf<EventsVO>()
    var scheduleListExtra = mutableListOf<EventExtraInfo>()

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
                        val modifiedEventIdx = modifiedEvent!!.id.toInt()
                        scheduleList[modifiedEventIdx] = modifiedEvent
                        scheduleCellAdaptor.notifyDataSetChanged()
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

        // TODO : 인공지능으로 일정들을 받을 수 있게 되면 없앨 부분
        scheduleList.apply {
            add(EventsVO(0,1,null,"회의",null,null, Color.parseColor("#111111"),Color.parseColor("#111111"),System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(1,1,null,"약 먹을 시간",null,null,-10572033,Color.parseColor("#876946"),System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(2,1,null,"외출",null,null,-10572033,Color.parseColor("#F8FF41"),System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(4,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
          }

        // TODO : 사진 여러장을 선택해도 우선은 처음 선택된 이미지로 통일. 위의 할 일과 마찬가지로 인공지능이 적용되면 맞춤형으로 코드가 바뀌어야 할 부분
        scheduleListExtra.apply{
            add(EventExtraInfo(0,0,imgList[0]))
            add(EventExtraInfo(0,0,imgList[0]))
            add(EventExtraInfo(0,0,imgList[0]))
            add(EventExtraInfo(0,0,imgList[0]))
            add(EventExtraInfo(0,0,imgList[0]))
        }

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
