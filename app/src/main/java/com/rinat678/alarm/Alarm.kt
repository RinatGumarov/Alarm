package com.rinat678.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Collections
import java.util.Comparator
import java.util.LinkedList
import java.util.Locale

class Alarm : Serializable, Comparable<Alarm> {

    //    private static final long serialVersionUID = 8699489847426803789L;
    var id: Int = 0
    var isAlarmActive = true
    private var alarmTime = Calendar.getInstance()
    var days = arrayOf(Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY, Day.SUNDAY)
    var alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
    var isVibrate = true
    var alarmName = "Alarm"
    var difficulty = Difficulty.EASY

    override fun compareTo(another: Alarm): Int {
        if (this.timeToCompare < another.timeToCompare)
            return -1
        else if (this.timeToCompare == another.timeToCompare)
            return 0
        else
            return 1
    }

    private val timeToCompare: Int
        get() = Integer.parseInt(alarmTimeString.replace(':', '0'))

    fun getAlarmTime(): Calendar {
        if (alarmTime.before(Calendar.getInstance()))
            alarmTime.add(Calendar.DAY_OF_MONTH, 1)
        while (!Arrays.asList(*days).contains(Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK) - 1])) {
            // day of week -1
            alarmTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        return alarmTime
    }

    fun setAlarmTime(time: String) {
        val pieces = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val newAlarmTime = Calendar.getInstance()
        newAlarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pieces[0]))
        newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(pieces[1]))
        newAlarmTime.set(Calendar.SECOND, 0)
        this.alarmTime = newAlarmTime
    }

    val alarmTimeString: String
        get() = SimpleDateFormat("H:mm", Locale.getDefault()).format(alarmTime.time)

    fun setAlarmTime(alarmTime: Calendar) {
        this.alarmTime = alarmTime
    }

    fun addDay(day: Day) {
        var contains = false
        for (d in days)
            if (d == day)
                contains = true
        if (!contains) {
            val result = LinkedList<Day>()
            Collections.addAll(result, *days)
            result.add(day)
            days = result.toArray<Day>(arrayOfNulls<Day>(result.size))
        }
    }

    fun removeDay(day: Day) {

        val result = LinkedList<Day>()
        for (d in days)
            if (d != day)
                result.add(d)
        days = result.toArray<Day>(arrayOfNulls<Day>(result.size))
    }

    // 5 буднейй
    // 2 выходней
    val repeatDaysString: String
        get() {
            val daysStringBuilder = StringBuilder()
            Arrays.sort(days) { lhs, rhs -> lhs.ordinal - rhs.ordinal }
            if (days.size == Day.values().size) {
                daysStringBuilder.append("Ежедневно")
            } else if (days.size == 5 && days[0] == Day.MONDAY && days[1] == Day.TUESDAY &&
                    days[2] == Day.WEDNESDAY && days[3] == Day.THURSDAY &&
                    days[4] == Day.FRIDAY) {
                daysStringBuilder.append("По Будням")
            } else if (days.size == 2 && days[1] == Day.SATURDAY && days[0] == Day.SUNDAY) {
                daysStringBuilder.append("По выходным")
            } else {
                for (d in days) {
                    daysStringBuilder.append(d.toString().substring(0, 3))
                    daysStringBuilder.append(',')
                }
                daysStringBuilder.setLength(daysStringBuilder.length - 1)
            }

            return daysStringBuilder.toString()
        }

    fun schedule(context: Context) {
        isAlarmActive = true

        val myIntent = Intent(context, AlarmAlertBroadcastReceiver::class.java)
        myIntent.putExtra("alarm", this)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().timeInMillis, pendingIntent)
    }

    val timeUntilNextAlarmMessage: String
        get() {
            val timeDifference = getAlarmTime().timeInMillis - System.currentTimeMillis()
            val days = timeDifference / (1000 * 60 * 60 * 24)
            val hours = timeDifference / (1000 * 60 * 60) - days * 24
            val minutes = timeDifference / (1000 * 60) - days * 24 * 60 - hours * 60
            val seconds = timeDifference / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60
            var alert = "Будильник прозвенит через "
            val sDays: String
            if (days % 10 > 4 || days > 10 && days < 15)
                sDays = String.format(Locale.getDefault(), "%d дней", days)
            else if ((days % 10).equals(1))
                sDays = String.format(Locale.getDefault(), "%d день", days)
            else
                sDays = String.format(Locale.getDefault(), "%d дня", days)

            val sHours: String
            if (hours % 10 > 4 || (days % 10).equals(0) || hours > 10 && hours < 15)
                sHours = String.format(Locale.getDefault(), "%d часов", hours)
            else if ((hours % 10).equals(1))
                sHours = String.format(Locale.getDefault(), "%d час", hours)
            else
                sHours = String.format(Locale.getDefault(), "%d часа", hours)

            val sMinutes: String
            if (minutes % 10 > 4 || (minutes % 10).equals(0) || minutes > 10 && minutes < 15)
                sMinutes = String.format(Locale.getDefault(), "%d минут и", minutes)
            else if ((minutes % 10).equals(1))
                sMinutes = String.format(Locale.getDefault(), "%d минуту и", minutes)
            else
                sMinutes = String.format(Locale.getDefault(), "%d минуты и", minutes)

            val sSecs: String
            if (seconds % 10 > 4 || (seconds % 10).equals(0) || seconds > 10 && seconds < 15)
                sSecs = String.format(Locale.getDefault(), "%d секунд", seconds)
            else if ((seconds % 10).equals(1))
                sSecs = String.format(Locale.getDefault(), "%d секунду", seconds)
            else
                sSecs = String.format(Locale.getDefault(), "%d секунды", seconds)


            if (days > 0)
                alert += "$sDays $sHours $sMinutes $sSecs"
            else if (hours > 0)
                alert += "$sHours $sMinutes $sSecs"
            else if (minutes > 0)
                alert += sMinutes + " " + sSecs
            else
                alert += sSecs
            return alert
        }

    enum class Difficulty {
        EASY;

        override fun toString(): String {
            when (this.ordinal) {
                0 -> return "Easy"
                1 -> return "Medium"
                2 -> return "Hard"
            }
            return super.toString()
        }
    }

    enum class Day {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;

        override fun toString(): String {
            when (this.ordinal) {
                0 -> return "Sunday"
                1 -> return "Monday"
                2 -> return "Tuesday"
                3 -> return "Wednesday"
                4 -> return "Thursday"
                5 -> return "Friday"
                6 -> return "Saturday"
            }
            return super.toString()
        }

    }
}
