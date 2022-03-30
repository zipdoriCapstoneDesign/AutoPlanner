package com.zipdori.autoplanner.modules.calendarprovider

class EventsVO(
    var id: Long,
    var title: String,
    var eventLocation: String?,
    var description: String?,
    var dtStart: String,
    var dtEnd: String?,
    var allDay: String,
    var duration: String?,
    var calendarId: String,
    var organizer: String,
    var guestCanModify: String
)