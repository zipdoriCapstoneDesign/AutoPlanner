package com.zipdori.autoplanner.modules

import android.content.Context
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommonModule(val context: Context) {
    fun getAllEventsAsHashmap() : HashMap<String, ArrayList<EventsVO>> {
        // 기존 일정 불러오기
        val schedules: HashMap<String, ArrayList<EventsVO>> = HashMap()

        val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(context!!)
        val allEvents: ArrayList<EventsVO> = calendarProviderModule.selectAllEvents()

        allEvents.forEach {
            if (it.deleted != 1) {
                val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)

                val start: Calendar = Calendar.getInstance()
                start.timeInMillis = it.dtStart
                start.set(Calendar.HOUR, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                if (it.dtEnd != null) {
                    val end: Calendar = Calendar.getInstance()
                    end.timeInMillis = it.dtEnd!!

                    while(start <= end) {
                        if (simpleDateFormat.format(start.time).equals(simpleDateFormat.format(end.time)) && it.allDay == 1) break

                        var tempArray: ArrayList<EventsVO>? = schedules.get(simpleDateFormat.format(start.time))
                        if (tempArray == null) {
                            tempArray = ArrayList()
                        }
                        tempArray.add(it)
                        schedules.put(simpleDateFormat.format(start.time), tempArray)

                        start.set(Calendar.DATE, start.get(Calendar.DATE) + 1)
                    }
                } else {
                    var tempArray: ArrayList<EventsVO>? = schedules.get(simpleDateFormat.format(start.time))
                    if (tempArray == null) {
                        tempArray = ArrayList()
                    }
                    tempArray.add(it)
                    schedules.put(simpleDateFormat.format(start.time), tempArray)
                }
            }
        }

        return schedules
    }

    fun getEventsVOArrayList(dateFormat : String) : ArrayList<EventsVO>? {
        val eventsVOArrayList: ArrayList<EventsVO>? = getAllEventsAsHashmap().get(dateFormat)
        if (eventsVOArrayList != null) {
            eventsVOArrayList.sortBy { it.dtStart }
        }

        return eventsVOArrayList
    }
}