package com.rinat678.alarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.TextView

import java.io.Serializable
import java.util.ArrayList

class AlarmPreferenceListAdapter(context: Context, alarm: Alarm) : BaseAdapter(), Serializable {

    var context: Context? = null
    private var alarm: Alarm? = null
    private val preferences = ArrayList<AlarmPreference>()
    val repeatDays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    val alarmTones: Array<String?>
    val alarmTonePaths: Array<String?>

    init {
        this.context = context

        Log.d("AlarmPreferenceListAda", "Loading Ringtones...")

        val ringtoneMgr = RingtoneManager(context)

        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM)

        val alarmsCursor = ringtoneMgr.cursor
        alarmTones = arrayOfNulls<String>(alarmsCursor.count + 1)
        alarmTones[0] = "Silent"
        alarmTonePaths = arrayOfNulls<String>(alarmsCursor.count + 1)
        alarmTonePaths[0] = ""

        if (alarmsCursor.moveToFirst()) {
            do {
                alarmTones[alarmsCursor.position + 1] = ringtoneMgr.getRingtone(alarmsCursor.position).getTitle(context)
                alarmTonePaths[alarmsCursor.position + 1] = ringtoneMgr.getRingtoneUri(alarmsCursor.position).toString()
            } while (alarmsCursor.moveToNext())
        }
        Log.d("AlarmPreferenceListAda", "Finished Loading " + alarmTones.size + " Ringtones.")
        alarmsCursor.close()

        setAlarm(alarm)
    }

    override fun getCount(): Int {
        return preferences.size
    }

    override fun getItem(position: Int): Any {
        return preferences[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val alarmPreference = getItem(position) as AlarmPreference
        val layoutInflater = LayoutInflater.from(context)
        when (alarmPreference.type) {
            AlarmPreference.Type.BOOLEAN -> {
                if (null == convertView)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null)

                val checkedTextView = convertView!!.findViewById(android.R.id.text1) as CheckedTextView
                checkedTextView.text = alarmPreference.title
                checkedTextView.isChecked = alarmPreference.value as Boolean
            }
            else -> {
                if (null == convertView)
                    convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)

                val text1 = convertView!!.findViewById(android.R.id.text1) as TextView
                text1.textSize = 18f
                text1.text = alarmPreference.title

                val text2 = convertView.findViewById(android.R.id.text2) as TextView
                text2.text = alarmPreference.summary
            }
        }

        return convertView
    }

    fun setAlarm(alarm: Alarm) {
        this.alarm = alarm
        preferences.clear()
        preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_NAME, "Надпись", alarm.alarmName, null, alarm.alarmName, AlarmPreference.Type.STRING))
        preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_TIME, "Установить время", alarm.alarmTimeString, null, alarm.alarmTime, AlarmPreference.Type.TIME))
        preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "Повторять", alarm.repeatDaysString, repeatDays, alarm.days, AlarmPreference.Type.MULTIPLE_LIST))

        val alarmToneUri = Uri.parse(alarm.alarmTonePath)
        val alarmTone = RingtoneManager.getRingtone(context, alarmToneUri)

        if (alarmTone is Ringtone && !alarm.alarmTonePath.equals("", ignoreCase = true)) {
            preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Мелодия", alarmTone.getTitle(context), alarmTones, alarm.alarmTonePath, AlarmPreference.Type.LIST))
        } else {
            preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Мелодия", alarmTones[0], alarmTones, null, AlarmPreference.Type.LIST))
        }

        preferences.add(AlarmPreference(AlarmPreference.Key.ALARM_VIBRATE, "Вибрация", null, null, alarm.isVibrate, AlarmPreference.Type.BOOLEAN))
    }

}