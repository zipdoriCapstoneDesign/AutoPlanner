package com.zipdori.autoplanner.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.Timestamp

class AutoPlannerModule(context: Context?) {
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

            val hashMap: HashMap<String, String> = HashMap()
            hashMap.put("_id", _id.toString())
            hashMap.put("start_time", startTime.toString())
            hashMap.put("end_time", endTime.toString())
            hashMap.put("title", title)
            hashMap.put("notes", notes)

            schedule.add(hashMap)
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes"))
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

            if (_id == id) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("_id", _id.toString())
                hashMap.put("start_time", startTime.toString())
                hashMap.put("end_time", endTime.toString())
                hashMap.put("title", title)
                hashMap.put("notes", notes)

                schedule.add(hashMap)
            }
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes"))

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

            if(timestamp in startTime..endTime) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("_id", _id.toString())
                hashMap.put("start_time", startTime.toString())
                hashMap.put("end_time", endTime.toString())
                hashMap.put("title", title)
                hashMap.put("notes", notes)

                schedule.add(hashMap)
            }
        }
        for (i in schedule) {
            println("_id=" + i.get("_id") +
                    ", start_time=" + i.get("start_time") +
                    ", end_time=" + i.get("end_time") +
                    ", title=" + i.get("title") +
                    ", notes=" + i.get("notes"))
        }
    }

    fun insertSchedule(startTime: Timestamp, endTime: Timestamp, title: String?, notes: String?) {
        val contentValues: ContentValues = ContentValues()
        contentValues.put("start_time ", startTime.toString())
        contentValues.put("end_time ", endTime.toString())
        contentValues.put("title ", title)
        contentValues.put("notes", notes)
        db.insert("schedule", null, contentValues)
    }

    fun deleteSchedule(id: Int) {
        db.delete("schedule", "_id=?", arrayOf(id.toString()))
    }

    fun updateSchedule(id: Int, startTime: Timestamp?, endTime: Timestamp?, title: String?, notes: String?) {
        val contentValues: ContentValues = ContentValues()
        if (startTime != null) contentValues.put("start_time ", startTime.toString())
        if (endTime != null) contentValues.put("end_time ", endTime.toString())
        if (title != null) contentValues.put("title ", title)
        if (notes != null) contentValues.put("notes", notes)

        db.update("schedule", contentValues, "_id=?", arrayOf("_id"))
    }
}