package com.zipdori.autoplanner.schedulegenerator

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Plan() :Parcelable{
    var fromData:Calendar = Calendar.getInstance()
    var toData:Calendar = Calendar.getInstance()

    constructor(parcel: Parcel) : this() {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plan> {
        override fun createFromParcel(parcel: Parcel): Plan {
            return Plan(parcel)
        }

        override fun newArray(size: Int): Array<Plan?> {
            return arrayOfNulls(size)
        }
    }
}
//class PlannerSideDate(){
//    var year:Int = 2022
//    var month:Int = 1
//    var day:Int = 1
//    var hour:Int = 8
//    var minute:Int = 0
//
//    fun generateDateString(): String {
//        return genStrForButtonDatePart()+" "+genStrForButtonTimePart()
//    }
//
//    fun genStrForButtonTimePart(): String {
//        val isPM = hour>=12
//        val isPMString = if(isPM)"오후" else "오전 "
//
//        val hourString:String = if(hour>12) {(hour-12).toString()} else {hour.toString()}
//        var minuteString:String = minute.toString()
//
//        //시간 표시할 때 3시 5분이 [3:5] 가 아니라 [3:05] 로 표시되게 하는 역할
//        if(minute<10) minuteString = "0$minuteString"
//
//
//
//        return "$isPMString$hourString:$minuteString"
//    }
//
//    fun genStrForButtonDatePart(): String {
//        var monthString:String = month.toString()
//        var dayString:String = day.toString()
//
//        if (month<10) monthString = "0$monthString"
//        if (day<10) dayString = "0$dayString"
//
//        return "$monthString"+"월 $dayString"+"일"
//    }
//}

//class ScheduleData() {
//    var fromDate: Calendar = Calendar.getInstance()
//    var toDate: Calendar = Calendar.getInstance()
//}

