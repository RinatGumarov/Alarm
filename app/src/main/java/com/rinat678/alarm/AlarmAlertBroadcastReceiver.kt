package com.rinat678.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmAlertBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(this.javaClass.simpleName, "onReceive")
        val alarmServiceIntent = Intent(
                context,
                AlarmServiceBroadcastReceiver::class.java)
        context.sendBroadcast(alarmServiceIntent, null)

        StaticWakeLock.lockOn(context)
        val bundle = intent.extras
        val alarm = bundle.getSerializable("alarm") as Alarm?

        val alarmAlertActivityIntent: Intent

        alarmAlertActivityIntent = Intent(context, AlarmAlertActivity::class.java)

        alarmAlertActivityIntent.putExtra("alarm", alarm)

        alarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(alarmAlertActivityIntent)
    }

}
