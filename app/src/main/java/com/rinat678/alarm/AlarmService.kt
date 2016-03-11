package com.rinat678.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

import java.util.Comparator
import java.util.TreeSet

class AlarmService : Service() {

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onCreate() {
        Log.d(this.javaClass.simpleName, "onCreate()")
        super.onCreate()
    }

    private val next: Alarm?
        get() {
            val alarmQueue = TreeSet(Comparator<Alarm> { lhs, rhs ->
                val diff = lhs.getAlarmTime().timeInMillis - rhs.getAlarmTime().timeInMillis
                if (diff > 0) {
                    return@Comparator 1
                } else if (diff < 0) {
                    return@Comparator -1
                }
                0
            })

            Database.init(applicationContext)
            val alarms = Database.all

            for (alarm in alarms) {
                if (alarm.isAlarmActive)
                    alarmQueue.add(alarm)
            }
            if (alarmQueue.iterator().hasNext()) {
                return alarmQueue.iterator().next()
            } else {
                return null
            }
        }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    override fun onDestroy() {
        Database.deactivate()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(this.javaClass.simpleName, "onStartCommand()")
        val alarm = next
        if (alarm != null) {
            alarm.schedule(applicationContext)
            Log.d(this.javaClass.simpleName, alarm.timeUntilNextAlarmMessage)

        } else {
            val myIntent = Intent(applicationContext, AlarmAlertBroadcastReceiver::class.java)
            myIntent.putExtra("alarm", Alarm())

            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(pendingIntent)
        }
        return Service.START_NOT_STICKY
    }

}
