package com.zipdori.autoplanner.schedulegenerator

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateForm(){
    companion object{
        val calYearForm: DateFormat = SimpleDateFormat("yyyy년")
        val calMdForm = SimpleDateFormat("MM월 dd일")
        val calhmForm = SimpleDateFormat("a h시 mm분", Locale.KOREA)
        val integratedForm = SimpleDateFormat("yyyy. MM. dd hh:mm")
    }
}

//class EventExtraInfo(
//    var _id:Long,
//    var event_id:Int,
//    var photo:Uri
//)
//
