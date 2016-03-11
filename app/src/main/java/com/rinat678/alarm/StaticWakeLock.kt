package com.rinat678.alarm

import android.content.Context
import android.os.PowerManager
import android.util.Log

object StaticWakeLock {
    private var wakeLock: PowerManager.WakeLock? = null

    fun lockOn(context: Context) {
        Log.d(this.javaClass.simpleName, "lockOn")
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (wakeLock == null)
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "ALARM")
        wakeLock!!.acquire()
    }

    fun lockOff(context: Context) {
        Log.d(this.javaClass.simpleName, "lockOff")
        try {
            if (wakeLock != null)
                wakeLock!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
