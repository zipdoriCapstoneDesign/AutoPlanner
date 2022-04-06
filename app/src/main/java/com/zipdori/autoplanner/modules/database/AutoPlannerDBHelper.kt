package com.zipdori.autoplanner.modules.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// TODO: 2022-04-06 Update 
class AutoPlannerDBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql: String = "CREATE TABLE IF NOT EXISTS schedule (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "title TEXT, " +
                "notes TEXT, " +
                "photo TEXT, " +
                "lat REAL, " +
                "lng REAL);"

        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val sql: String = "DROP TABLE IF EXISTS schedule"

        db?.execSQL(sql)
        onCreate(db)
    }

    fun initDB(db: SQLiteDatabase?) {
        val sql: String = "DROP TABLE IF EXISTS schedule"

        db?.execSQL(sql)
        onCreate(db)
    }

    fun initSchedule(db: SQLiteDatabase?) {
        val sqlDropSchedule: String = "DROP TABLE IF EXISTS schedule"
        db?.execSQL(sqlDropSchedule)

        val sqlCreateSchedule: String = "CREATE TABLE IF NOT EXISTS schedule (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "title TEXT, " +
                "notes TEXT, " +
                "photo TEXT, " +
                "lat REAL, " +
                "lng REAL);"
        db?.execSQL(sqlCreateSchedule)
    }
}