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

import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.ActivityListupSchedulecellBinding
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.util.*

class ListupSchedulecellActivity : AppCompatActivity() , View.OnClickListener {

    private lateinit var binding: ActivityListupSchedulecellBinding
    lateinit var scheduleCellAdaptor:ScheduleCellAdapter
    val scheduleList = mutableListOf<EventsVO>()
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
            add(EventsVO(5,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(6,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(7,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(8,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
            add(EventsVO(9,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"Asia/Seoul",null,null,null,null,null,null,null))
        }

        val scheduleListBool = BooleanArray(scheduleList.size) { true }
        scheduleCellAdaptor.scheduleList = scheduleList
        scheduleCellAdaptor.scheduleListBool = scheduleListBool
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sc_cancelButton-> {
                finish()
            }
        }
    }
}
