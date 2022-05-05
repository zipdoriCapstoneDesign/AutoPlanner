package com.zipdori.autoplanner.schedulegenerator


import android.app.Application
import android.content.Context
import android.net.Uri

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateForm(){
    companion object{
        val calYearForm: DateFormat = SimpleDateFormat("yyyy년\n")
        val calMdForm = SimpleDateFormat("MM월 dd일")
        val calhmForm = SimpleDateFormat("a h시 mm분", Locale.KOREA)
        val integratedForm = SimpleDateFormat("yyyy. MM. dd hh:mm")
    }
}




