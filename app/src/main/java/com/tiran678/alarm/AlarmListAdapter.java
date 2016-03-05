package com.tiran678.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.tiran678.hardcorealarm.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmListAdapter extends BaseAdapter {

    private AlarmActivity alarmActivity;
    List<Alarm> alarms = new ArrayList<Alarm>();

    public static final String ALARM_FIELDS[] = { Database.COLUMN_ALARM_ACTIVE,
            Database.COLUMN_ALARM_TIME, Database.COLUMN_ALARM_DAYS };


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
                    R.layout.alarm_list_element, null);

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

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//    }
//
    @Override
    public long getItemId(int position) {
        return position;
    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        Collections.sort(alarms);
        this.alarms = alarms;
    }


}
