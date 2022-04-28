package com.zipdori.autoplanner.modules.calendarprovider

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
class EventsVO(
    var id: Long,
    var calendarId: Int,
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
