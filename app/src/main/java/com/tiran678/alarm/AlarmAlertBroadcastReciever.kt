package com.tiran678.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AlarmAlertBroadcastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mathAlarmServiceIntent = Intent(
                context,
                AlarmServiceBroadcastReciever::class.java)
        context.sendBroadcast(mathAlarmServiceIntent, null)

        StaticWakeLock.lockOn(context)
        val bundle = intent.extras
        val alarm = bundle.getSerializable("alarm") as Alarm?

        val mathAlarmAlertActivityIntent: Intent

        mathAlarmAlertActivityIntent = Intent(context, AlarmAlertActivity::class.java)

        mathAlarmAlertActivityIntent.putExtra("alarm", alarm)

        mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(mathAlarmAlertActivityIntent)
    }

}
