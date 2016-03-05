package com.tiran678.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PhoneStateChangedBroadcastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(javaClass.simpleName, "onReceive()")

    }

}

