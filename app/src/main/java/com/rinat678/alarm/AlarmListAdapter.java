package com.rinat678.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AlarmListAdapter extends BaseAdapter {

    private final AlarmActivity alarmActivity;
    private List<Alarm> alarms = new ArrayList<>();


    public AlarmListAdapter(AlarmActivity activity) {
        this.alarmActivity = activity;
    }

    @Override
    public int getCount(){

        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(alarmActivity).inflate(
                    R.layout.alarm_list_element, parent, false);

        Alarm alarm = (Alarm) getItem(position);

        Switch aSwitch = (Switch) convertView.findViewById(R.id.checkBox_alarm_active);
        aSwitch.setChecked(alarm.isAlarmActive());
        aSwitch.setTag(position);
        aSwitch.setOnClickListener(alarmActivity);

        TextView alarmTimeView = (TextView) convertView
                .findViewById(R.id.textView_alarm_time);
        alarmTimeView.setText(alarm.getAlarmTimeString());


        TextView alarmDaysView = (TextView) convertView
                .findViewById(R.id.textView_alarm_days);
        alarmDaysView.setText(alarm.getRepeatDaysString());


        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setAlarms(List<Alarm> alarms) {
        Collections.sort(alarms);
        this.alarms = alarms;
    }


}
