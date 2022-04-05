package com.zipdori.autoplanner.modules.calendarprovider

class CalendarsVO(
    var id: Long,
    var name: String,
    var calendarDisplayName: String,
    var visible: Int?,
    var syncEvents: Int?,
    var accountName: String?,
    var accountType: String?,
    var calendarColor: Int,
    var calendarAccessLevel: Int?,
    var ownerAccount: String?,
    var calendarTimeZone: String?,
    var allowedReminders: String?,
    var allowedAvailability: String?,
    var allowedAttendeesTypes: String?
)