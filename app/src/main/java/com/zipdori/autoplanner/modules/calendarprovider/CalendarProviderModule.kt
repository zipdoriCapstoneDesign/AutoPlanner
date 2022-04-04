package com.zipdori.autoplanner.modules.calendarprovider

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract

class CalendarProviderModule(context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun selectAllEvents(): ArrayList<EventsVO> {
        val EVENT_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DISPLAY_COLOR,
            CalendarContract.Events.EVENT_COLOR,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.GUESTS_CAN_MODIFY,
            CalendarContract.Events.DELETED
        )

        val PROJECTION_ID_INDEX: Int = 0
        val PROJECTION_CALENDAR_ID_INDEX: Int = 1
        val PROJECTION_ORGANIZER_INDEX: Int = 2
        val PROJECTION_TITLE_INDEX: Int = 3
        val PROJECTION_EVENT_LOCATION_INDEX: Int = 4
        val PROJECTION_DESCRIPTION_INDEX: Int = 5
        val PROJECTION_DISPLAY_COLOR_INDEX: Int = 6
        val PROJECTION_EVENT_COLOR_INDEX: Int = 7
        val PROJECTION_DTSTART_INDEX: Int = 8
        val PROJECTION_DTEND_INDEX: Int = 9
        val PROJECTION_EVENT_TIMEZONE_INDEX: Int = 10
        val PROJECTION_DURATION_INDEX: Int = 11
        val PROJECTION_ALL_DAY_INDEX: Int = 12
        val PROJECTION_RRULE_INDEX: Int = 13
        val PROJECTION_RDATE_INDEX: Int = 14
        val PROJECTION_AVAILABILITY_INDEX: Int = 15
        val PROJECTION_GUESTS_CAN_MODIFY_INDEX: Int = 16
        val PROJECTION_DELETED_INDEX: Int = 17


        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val cur: Cursor = contentResolver.query(uri, EVENT_PROJECTION, null, null, null)!!

        val eventsVOArrayList: ArrayList<EventsVO> = ArrayList()
        while (cur.moveToNext()) {
            val eventsVO: EventsVO = EventsVO(
                cur.getLong(PROJECTION_ID_INDEX),
                cur.getInt(PROJECTION_CALENDAR_ID_INDEX),
                cur.getString(PROJECTION_ORGANIZER_INDEX),
                cur.getString(PROJECTION_TITLE_INDEX),
                cur.getString(PROJECTION_EVENT_LOCATION_INDEX),
                cur.getString(PROJECTION_DESCRIPTION_INDEX),
                cur.getInt(PROJECTION_DISPLAY_COLOR_INDEX),
                cur.getInt(PROJECTION_EVENT_COLOR_INDEX),
                cur.getLong(PROJECTION_DTSTART_INDEX),
                cur.getLong(PROJECTION_DTEND_INDEX),
                cur.getString(PROJECTION_EVENT_TIMEZONE_INDEX),
                cur.getString(PROJECTION_DURATION_INDEX),
                cur.getInt(PROJECTION_ALL_DAY_INDEX),
                cur.getString(PROJECTION_RRULE_INDEX),
                cur.getString(PROJECTION_RDATE_INDEX),
                cur.getInt(PROJECTION_AVAILABILITY_INDEX),
                cur.getInt(PROJECTION_GUESTS_CAN_MODIFY_INDEX),
                cur.getInt(PROJECTION_DELETED_INDEX)
                )
            eventsVOArrayList.add(eventsVO)
        }

        return eventsVOArrayList
    }

    fun insertEvent(
        calendarId: Int,
        title: String?,
        eventLocation: String?,
        description: String?,
        eventColor: Int?,
        dtStart: Long,
        dtEnd: Long,
        eventTimeZone: String,
        duration: String?,
        allDay: Int?,
        rRule: String?,
        rDate: String?,
    ) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.EVENT_LOCATION, eventLocation)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.EVENT_COLOR, eventColor)
            put(CalendarContract.Events.DESCRIPTION, "temp event")
            put(CalendarContract.Events.DTSTART, dtStart)
            put(CalendarContract.Events.DTEND, dtEnd)
            put(CalendarContract.Events.EVENT_TIMEZONE, eventTimeZone)
            put(CalendarContract.Events.DURATION, duration)
            put(CalendarContract.Events.ALL_DAY, allDay)
            put(CalendarContract.Events.RRULE, rRule)
            put(CalendarContract.Events.RDATE, rDate)
        }
        val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
    }
}