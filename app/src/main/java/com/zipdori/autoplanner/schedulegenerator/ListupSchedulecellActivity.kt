package com.zipdori.autoplanner.schedulegenerator

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
        scheduleCellAdaptor= ScheduleCellAdapter(this)
        binding.rvScheduleList.adapter = scheduleCellAdaptor

        val now = System.currentTimeMillis()
        val tDate = Date(now)
        val t2Date = Date(now+100000)
        val dFormat = DateForm.integratedForm.format(tDate)
        val d2Format = DateForm.integratedForm.format(t2Date)
        scheduleList.apply {
            add(EventsVO(1,1,null,"회의",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(2,1,null,"약 먹을 시간",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
            add(EventsVO(3,1,null,"외출",null,null,-10572033,null,System.currentTimeMillis(),System.currentTimeMillis()+60000,"idk",null,null,null,null,null,null,null))
        }
        scheduleCellAdaptor.scheduleList = scheduleList
        scheduleCellAdaptor.notifyDataSetChanged()

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.sc_cancelButton-> {
                finish()
            }
        }
    }
}
