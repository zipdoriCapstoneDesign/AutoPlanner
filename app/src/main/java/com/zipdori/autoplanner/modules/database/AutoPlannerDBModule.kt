package com.zipdori.autoplanner.modules.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class AutoPlannerDBModule(context: Context?) {
    private var autoPlannerDBHelper: AutoPlannerDBHelper
    private var db: SQLiteDatabase

    init {
        this.autoPlannerDBHelper = AutoPlannerDBHelper(context, "AUTOPLANNER.db", null, 1)
        this.db = autoPlannerDBHelper.writableDatabase
        autoPlannerDBHelper.onCreate(db)
    }

    fun initExtraInfo() {
        autoPlannerDBHelper.initSchedule(db)
    }

    fun selectAllExtraInfo() {
        val cursor: Cursor = db.query("extra_info", null, null, null, null, null, null, null)

        val schedule: ArrayList<HashMap<String, String>> = ArrayList()
        while (cursor.moveToNext()) {
            val _id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val eventId: Long = cursor.getLong(cursor.getColumnIndexOrThrow("event_id"))
            val photo: String = cursor.getString(cursor.getColumnIndexOrThrow("photo"))


            val hashMap: HashMap<String, String> = HashMap()
            hashMap.put("_id", _id.toString())
            hashMap.put("event_id", eventId.toString())
            hashMap.put("photo", photo)

            schedule.add(hashMap)
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", event_id=" + i.get("event_id") +
                    ", photo=" + i.get("photo"))
        }
    }

    fun selectExtraInfo(id: Int) {
        val cursor: Cursor = db.query("extra_info", null, null, null, null, null, null, null)

        val schedule: ArrayList<HashMap<String, String>> = ArrayList()
        while (cursor.moveToNext()) {
            val _id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val eventId: Long = cursor.getLong(cursor.getColumnIndexOrThrow("event_id"))
            val photo: String = cursor.getString(cursor.getColumnIndexOrThrow("photo"))

            if (_id == id) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("_id", _id.toString())
                hashMap.put("event_id", eventId.toString())
                hashMap.put("photo", photo)

                schedule.add(hashMap)
            }
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", event_id=" + i.get("event_id") +
                    ", photo=" + i.get("photo"))
        }
    }

    fun insertExtraInfo(eventId: Long, photo: String?) {
        val contentValues: ContentValues = ContentValues()
        contentValues.put("event_id", eventId.toInt())

        contentValues.put("photo", photo)
        db.insert("extra_info", null, contentValues)
    }

    fun deleteExtraInfo(id: Int) {
        db.delete("extra_info", "_id=?", arrayOf(id.toString()))
    }

    fun updateExtraInfo(id: Int, eventId: Long, photo: String?) {
        val contentValues: ContentValues = ContentValues()
        if (eventId != null) contentValues.put("event_id", eventId.toInt())
        if (photo != null) contentValues.put("photo", photo)

        db.update("extra_info", contentValues, "_id=?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun selectExtraInfoByEventId(eventId: Long): EventExtraInfoVO {
        val select = "select * from extra_info where event_id = $eventId"
        val cursor = db.rawQuery(select, null)

        cursor.moveToNext()
        val extractedId = cursor.getInt(cursor.getColumnIndex("_id"))
        val extractedEventId = cursor.getLong(cursor.getColumnIndex("event_id"))
        val extractedUri = cursor.getString(cursor.getColumnIndex("photo"))
        return EventExtraInfoVO(extractedId,extractedEventId, Uri.parse(extractedUri))
    }
}