package com.zipdori.autoplanner.modules.calendarprovider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract

class CalendarProviderModule(context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun selectAllEvents(): ArrayList<EventsVO> {
        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        val EVENT_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events.GUESTS_CAN_MODIFY
        )

        // The indices for the projection array above.
        val PROJECTION_ID_INDEX: Int = 0
        val PROJECTION_TITLE_INDEX: Int = 1
        val PROJECTION_EVENT_LOCATION_INDEX: Int = 2
        val PROJECTION_DESCRIPTION_INDEX: Int = 3
        val PROJECTION_DTSTART_INDEX: Int = 4
        val PROJECTION_DTEND_INDEX: Int = 5
        val PROJECTION_ALL_DAY_INDEX: Int = 6
        val PROJECTION_DURATION_INDEX: Int = 7
        val PROJECTION_CALENDAR_ID_INDEX: Int = 8
        val PROJECTION_ORGANIZER_INDEX: Int = 9
        val PROJECTION_GUESTS_CAN_MODIFY_INDEX: Int = 10

        // Run query
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        /*
        val selection: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
                "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
                "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
        val selectionArgs: Array<String> = arrayOf("kdlrlgusk@naver.com", "com.example")
        val cur: Cursor = context!!.contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)!!

         */
        val cur: Cursor = contentResolver.query(uri, EVENT_PROJECTION, null, null, null)!!

        val eventsVOArrayList: ArrayList<EventsVO> = ArrayList()
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            // Get the field values
            val eventsVO: EventsVO = EventsVO(
                cur.getLong(PROJECTION_ID_INDEX),
                cur.getString(PROJECTION_TITLE_INDEX),
                cur.getString(PROJECTION_EVENT_LOCATION_INDEX),
                cur.getString(PROJECTION_DESCRIPTION_INDEX),
                cur.getString(PROJECTION_DTSTART_INDEX),
                cur.getString(PROJECTION_DTEND_INDEX),
                cur.getString(PROJECTION_ALL_DAY_INDEX),
                cur.getString(PROJECTION_DURATION_INDEX),
                cur.getString(PROJECTION_CALENDAR_ID_INDEX),
                cur.getString(PROJECTION_ORGANIZER_INDEX),
                cur.getString(PROJECTION_GUESTS_CAN_MODIFY_INDEX)
                )
            eventsVOArrayList.add(eventsVO)
        }

        return eventsVOArrayList
    }
}