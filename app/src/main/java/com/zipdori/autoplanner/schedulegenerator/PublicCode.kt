package com.zipdori.autoplanner.schedulegenerator

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
    }
}

