package com.zipdori.autoplanner.modules.calendarprovider

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class EventsVO(
    var id: Long,
    var calendarId: Long,
    var organizer: String?,
    var title: String?,
    var eventLocation: String?,
    var description: String?,
    var displayColor: Int,
    var eventColor: Int?,
    var dtStart: Long,
    var dtEnd: Long?,
    var eventTimeZone: String,
    var duration: String?,
    var allDay: Int?,
    var rRule: String?,
    var rDate: String?,
    var availability: Int?,
    var guestCanModify: Int?,
    var deleted: Int?
) : Parcelable

@Parcelize
class EventExtraInfo(
    var _id:Int,
    var event_id:Long,
    var photo: Uri?
) : Parcelable