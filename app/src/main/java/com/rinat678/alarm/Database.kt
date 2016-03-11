package com.rinat678.alarm

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList

internal class Database private constructor(context: Context) : SQLiteOpenHelper(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(this.javaClass.simpleName, "db created")
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS $ALARM_TABLE ( $COLUMN_ALARM_ID INTEGER primary key autoincrement, $COLUMN_ALARM_ACTIVE INTEGER NOT NULL, $COLUMN_ALARM_TIME TEXT NOT NULL, $COLUMN_ALARM_DAYS BLOB NOT NULL, $COLUMN_ALARM_DIFFICULTY INTEGER NOT NULL, $COLUMN_ALARM_TONE TEXT NOT NULL, $COLUMN_ALARM_VIBRATE INTEGER NOT NULL, $COLUMN_ALARM_NAME TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(this.javaClass.simpleName, "updated")
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE)
        onCreate(db)
    }

    companion object {
        private var instance: Database? = null
        private var db: SQLiteDatabase? = null

        private val DATABASE_NAME = "DB"
        private val DATABASE_VERSION = 1

        private val ALARM_TABLE = "alarm"
        private val COLUMN_ALARM_ID = "_id"
        private val COLUMN_ALARM_ACTIVE = "alarm_active"
        private val COLUMN_ALARM_TIME = "alarm_time"
        private val COLUMN_ALARM_DAYS = "alarm_days"
        private val COLUMN_ALARM_DIFFICULTY = "alarm_difficulty"
        private val COLUMN_ALARM_TONE = "alarm_tone"
        private val COLUMN_ALARM_VIBRATE = "alarm_vibrate"
        private val COLUMN_ALARM_NAME = "alarm_name"

        fun init(context: Context) {
            if (null == instance) {
                instance = Database(context)
            }
        }

        private fun getDatabase(): SQLiteDatabase {
            if (null == db) {
                db = instance!!.writableDatabase
            }
            return db!!
        }

        fun deactivate() {
            if (db != null && db!!.isOpen) {
                db!!.close()
            }
            db = null
            instance = null
        }

        fun create(alarm: Alarm) {
            val cv = ContentValues()
            cv.put(COLUMN_ALARM_ACTIVE, alarm.isAlarmActive)
            cv.put(COLUMN_ALARM_TIME, alarm.alarmTimeString)

            try {
                val bos = ByteArrayOutputStream()
                val oos: ObjectOutputStream
                oos = ObjectOutputStream(bos)
                oos.writeObject(alarm.days)
                val buff = bos.toByteArray()

                cv.put(COLUMN_ALARM_DAYS, buff)

            } catch (ignored: Exception) {
            }

            cv.put(COLUMN_ALARM_DIFFICULTY, alarm.difficulty.ordinal)
            cv.put(COLUMN_ALARM_TONE, alarm.alarmTonePath)
            cv.put(COLUMN_ALARM_VIBRATE, alarm.isVibrate)
            cv.put(COLUMN_ALARM_NAME, alarm.alarmName)

            getDatabase().insert(ALARM_TABLE, null, cv)
        }

        fun update(alarm: Alarm) {
            val cv = ContentValues()
            cv.put(COLUMN_ALARM_ACTIVE, alarm.isAlarmActive)
            cv.put(COLUMN_ALARM_TIME, alarm.alarmTimeString)

            try {
                val bos = ByteArrayOutputStream()
                val oos: ObjectOutputStream
                oos = ObjectOutputStream(bos)
                oos.writeObject(alarm.days)
                val buff = bos.toByteArray()

                cv.put(COLUMN_ALARM_DAYS, buff)

            } catch (ignored: Exception) {
            }

            cv.put(COLUMN_ALARM_DIFFICULTY, alarm.difficulty.ordinal)
            cv.put(COLUMN_ALARM_TONE, alarm.alarmTonePath)
            cv.put(COLUMN_ALARM_VIBRATE, alarm.isVibrate)
            cv.put(COLUMN_ALARM_NAME, alarm.alarmName)

            getDatabase().update(ALARM_TABLE, cv, "_id=" + alarm.id, null)
        }

        fun deleteEntry(alarm: Alarm) {
            deleteEntry(alarm.id)
        }

        private fun deleteEntry(id: Int) {
            getDatabase().delete(ALARM_TABLE, COLUMN_ALARM_ID + "=" + id, null)
        }

        fun deleteAll() {
            getDatabase().delete(ALARM_TABLE, "1", null)
        }

        private // TODO Auto-generated method stub
        val cursor: Cursor
            get() {
                val columns = arrayOf(COLUMN_ALARM_ID, COLUMN_ALARM_ACTIVE, COLUMN_ALARM_TIME, COLUMN_ALARM_DAYS, COLUMN_ALARM_DIFFICULTY, COLUMN_ALARM_TONE, COLUMN_ALARM_VIBRATE, COLUMN_ALARM_NAME)
                return getDatabase().query(ALARM_TABLE, columns, null, null, null, null,
                        null)
            }

        // COLUMN_ALARM_ID,
        // COLUMN_ALARM_ACTIVE,
        // COLUMN_ALARM_TIME,
        // COLUMN_ALARM_DAYS,
        // COLUMN_ALARM_DIFFICULTY,
        // COLUMN_ALARM_TONE,
        // COLUMN_ALARM_VIBRATE,
        // COLUMN_ALARM_NAME
        val all: List<Alarm>
            get() {
                val alarms = ArrayList<Alarm>()
                val cursor = Database.cursor
                if (cursor.moveToFirst()) {

                    do {

                        val alarm = Alarm()
                        alarm.id = cursor.getInt(0)
                        alarm.isAlarmActive = cursor.getInt(1) == 1
                        alarm.setAlarmTime(cursor.getString(2))
                        val repeatDaysBytes = cursor.getBlob(3)

                        val byteArrayInputStream = ByteArrayInputStream(
                                repeatDaysBytes)
                        try {
                            val objectInputStream = ObjectInputStream(
                                    byteArrayInputStream)
                            val repeatDays: Array<Alarm.Day>
                            val `object` = objectInputStream.readObject()
                            if (`object` is Array<*>) {
                                repeatDays = `object` as Array<Alarm.Day>
                                alarm.days = repeatDays
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: ClassNotFoundException) {
                            e.printStackTrace()
                        }

                        alarm.difficulty = Alarm.Difficulty.values()[cursor.getInt(4)]
                        alarm.alarmTonePath = cursor.getString(5)
                        alarm.isVibrate = cursor.getInt(6) == 1
                        alarm.alarmName = cursor.getString(7)

                        alarms.add(alarm)

                    } while (cursor.moveToNext())
                }
                cursor.close()
                return alarms
            }
    }
}