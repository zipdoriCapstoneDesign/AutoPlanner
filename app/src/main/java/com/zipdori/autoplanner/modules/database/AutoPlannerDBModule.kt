package com.zipdori.autoplanner.modules.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.Timestamp

class AutoPlannerDBModule(context: Context?) {
    private var autoPlannerDBHelper: AutoPlannerDBHelper
    private var db: SQLiteDatabase

    init {
        this.autoPlannerDBHelper = AutoPlannerDBHelper(context, "AUTOPLANNER.db", null, 1)
        this.db = autoPlannerDBHelper.writableDatabase
        autoPlannerDBHelper.onCreate(db)
    }

    fun initSchedule() {
        autoPlannerDBHelper.initSchedule(db)
    }

    fun selectAllSchedule() {
        val cursor: Cursor = db.query("schedule", null, null, null, null, null, null, null)

        val schedule: ArrayList<HashMap<String, String>> = ArrayList()
        while (cursor.moveToNext()) {
            val _id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val startTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("start_time")))
            val endTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("end_time")))
            val title: String = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val notes: String = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            val photo: String = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
            val lat: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lat"))
            val lng: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lng"))

            val hashMap: HashMap<String, String> = HashMap()
            hashMap.put("_id", _id.toString())
            hashMap.put("start_time", startTime.toString())
            hashMap.put("end_time", endTime.toString())
            hashMap.put("title", title)
            hashMap.put("notes", notes)
            hashMap.put("photo", photo)
            hashMap.put("lat", lat.toString())
            hashMap.put("lng", lng.toString())

            schedule.add(hashMap)
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes") +
                    ", photo=" + i.get("photo") +
                    ", lat=" + i.get("lat") +
                    ", lng=" + i.get("lng"))
        }
    }

    fun selectSchedule(id: Int) {
        val cursor: Cursor = db.query("schedule", null, null, null, null, null, null, null)

        val schedule: ArrayList<HashMap<String, String>> = ArrayList()
        while (cursor.moveToNext()) {
            val _id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val startTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("start_time")))
            val endTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("end_time")))
            val title: String = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val notes: String = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            val photo: String = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
            val lat: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lat"))
            val lng: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lng"))

            if (_id == id) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("_id", _id.toString())
                hashMap.put("start_time", startTime.toString())
                hashMap.put("end_time", endTime.toString())
                hashMap.put("title", title)
                hashMap.put("notes", notes)
                hashMap.put("photo", photo)
                hashMap.put("lat", lat.toString())
                hashMap.put("lng", lng.toString())

                schedule.add(hashMap)
            }
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes") +
                    ", photo=" + i.get("photo") +
                    ", lat=" + i.get("lat") +
                    ", lng=" + i.get("lng"))
        }
    }

    fun selectScheduleByTime(timestamp: Timestamp) {
        val cursor: Cursor = db.query("schedule", null, null, null, null, null, null, null)

        val schedule: ArrayList<HashMap<String, String>> = ArrayList()
        while (cursor.moveToNext()) {
            val _id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val startTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("start_time")))
            val endTime: Timestamp = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("end_time")))
            val title: String = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val notes: String = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            val photo: String = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
            val lat: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lat"))
            val lng: Float = cursor.getFloat(cursor.getColumnIndexOrThrow("lng"))

            if(timestamp in startTime..endTime) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("_id", _id.toString())
                hashMap.put("start_time", startTime.toString())
                hashMap.put("end_time", endTime.toString())
                hashMap.put("title", title)
                hashMap.put("notes", notes)
                hashMap.put("photo", photo)
                hashMap.put("lat", lat.toString())
                hashMap.put("lng", lng.toString())

                schedule.add(hashMap)
            }
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes") +
                    ", photo=" + i.get("photo") +
                    ", lat=" + i.get("lat") +
                    ", lng=" + i.get("lng"))
        }
    }

    fun insertSchedule(startTime: Timestamp, endTime: Timestamp, title: String?, notes: String?, photo: String?, lat: Float?, lng: Float?) {
        val contentValues: ContentValues = ContentValues()
        contentValues.put("start_time ", startTime.toString())
        contentValues.put("end_time ", endTime.toString())
        contentValues.put("title ", title)
        contentValues.put("notes", notes)
        contentValues.put("photo", photo)
        contentValues.put("lat", lat)
        contentValues.put("lng", lng)
        db.insert("schedule", null, contentValues)
    }

    fun deleteSchedule(id: Int) {
        db.delete("schedule", "_id=?", arrayOf(id.toString()))
    }

    fun updateSchedule(id: Int, startTime: Timestamp?, endTime: Timestamp?, title: String?, notes: String?, photo: String?, lat: Float?, lng: Float?) {
        val contentValues: ContentValues = ContentValues()
        if (startTime != null) contentValues.put("start_time ", startTime.toString())
        if (endTime != null) contentValues.put("end_time ", endTime.toString())
        if (title != null) contentValues.put("title ", title)
        if (notes != null) contentValues.put("notes", notes)
        if (photo != null) contentValues.put("photo", photo)
        if (lat != null) contentValues.put("lat", lat)
        if (lng != null) contentValues.put("lng", lng)

        db.update("schedule", contentValues, "_id=?", arrayOf("_id"))
    }
}