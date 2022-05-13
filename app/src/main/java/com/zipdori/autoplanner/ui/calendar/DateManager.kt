package com.toyproject.testproject3_zipdori.ui.home

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateManager(val calendar: Calendar) {

    fun getDays(): ArrayList<Date> {
        val startDate: Date = calendar.time

        val count: Int = getWeeks() * 7

        calendar.set(Calendar.DATE, 1)
        val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DATE, -dayOfWeek)

        val days: ArrayList<Date> = ArrayList()

        for (i in 0 until count) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        calendar.time = startDate

        return days
    }

    fun isCurrentMonth(date: Date): Boolean {
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM", Locale.US)
        val currentMonth: String = simpleDateFormat.format(calendar.time)
        return currentMonth.equals(simpleDateFormat.format(date))
    }

    fun getWeeks(): Int {
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    fun getDayOfWeek(date: Date): Int {
        val calendar: Calendar = Calendar.getInstance();
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
    }
}