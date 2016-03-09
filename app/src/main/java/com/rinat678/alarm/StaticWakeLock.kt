package com.rinat678.alarm

import android.content.Context
import android.os.PowerManager

object StaticWakeLock {
    private var wakeLock: PowerManager.WakeLock? = null

    fun lockOn(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (wakeLock == null)
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "ALARM")
        wakeLock!!.acquire()
    }

    fun lockOff(context: Context) {
        try {
            if (wakeLock != null)
                wakeLock!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
