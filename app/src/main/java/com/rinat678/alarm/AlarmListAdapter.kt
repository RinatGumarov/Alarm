package com.rinat678.alarm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView

import java.util.ArrayList
import java.util.Collections

internal class AlarmListAdapter(private val alarmActivity: AlarmActivity) : BaseAdapter() {
    private var alarms: List<Alarm> = ArrayList()

    override fun getCount(): Int {

        return alarms.size
    }

    override fun getItem(position: Int): Any {
        return alarms[position]
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d(this.javaClass.simpleName, "getView")
        var cv = convertView
        if (cv == null)
            cv = LayoutInflater.from(alarmActivity).inflate(
                    R.layout.alarm_list_element, parent, false)

        val alarm = getItem(position) as Alarm

        val aSwitch = cv!!.findViewById(R.id.checkBox_alarm_active) as Switch
        aSwitch.isChecked = alarm.isAlarmActive
        aSwitch.tag = position
        aSwitch.setOnClickListener(alarmActivity)

        val alarmTimeView = cv.findViewById(R.id.textView_alarm_time) as TextView
        alarmTimeView.text = alarm.alarmTimeString


        val alarmDaysView = cv.findViewById(R.id.textView_alarm_days) as TextView
        alarmDaysView.text = alarm.repeatDaysString


        return cv

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setAlarms(alarms: List<Alarm>) {
        Collections.sort(alarms)
        this.alarms = alarms
    }


}
