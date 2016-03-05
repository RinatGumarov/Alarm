package com.tiran678.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmServiceBroadcastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmServiceBroadcastReciever", "onReceive()")
        val serviceIntent = Intent(context, AlarmService::class.java)
        context.startService(serviceIntent)
    }

}
