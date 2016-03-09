package com.rinat678.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

class Database extends SQLiteOpenHelper {
    private static Database instance = null;
    private static SQLiteDatabase database = null;

    private static final String DATABASE_NAME = "DB";
    private static final int DATABASE_VERSION = 1;

    private static final String ALARM_TABLE = "alarm";
    private static final String COLUMN_ALARM_ID = "_id";
    private static final String COLUMN_ALARM_ACTIVE = "alarm_active";
    private static final String COLUMN_ALARM_TIME = "alarm_time";
    private static final String COLUMN_ALARM_DAYS = "alarm_days";
    private static final String COLUMN_ALARM_DIFFICULTY = "alarm_difficulty";
    private static final String COLUMN_ALARM_TONE = "alarm_tone";
    private static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";
    private static final String COLUMN_ALARM_NAME = "alarm_name";

    public static void init(Context context) {
        if (null == instance) {
            instance = new Database(context);
        }
    }

    private static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }

    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }

    public static void create(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_ACTIVE, alarm.isAlarmActive());
        cv.put(COLUMN_ALARM_TIME, alarm.getAlarmTimeString());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(alarm.getDays());
            byte[] buff = bos.toByteArray();

            cv.put(COLUMN_ALARM_DAYS, buff);

        } catch (Exception ignored){
        }

        cv.put(COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty().ordinal());
        cv.put(COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
        cv.put(COLUMN_ALARM_VIBRATE, alarm.isVibrate());
        cv.put(COLUMN_ALARM_NAME, alarm.getAlarmName());

        getDatabase().insert(ALARM_TABLE, null, cv);
    }
    public static void update(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_ACTIVE, alarm.isAlarmActive());
        cv.put(COLUMN_ALARM_TIME, alarm.getAlarmTimeString());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(alarm.getDays());
            byte[] buff = bos.toByteArray();

            cv.put(COLUMN_ALARM_DAYS, buff);

        } catch (Exception ignored){
        }

        cv.put(COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty().ordinal());
        cv.put(COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
        cv.put(COLUMN_ALARM_VIBRATE, alarm.isVibrate());
        cv.put(COLUMN_ALARM_NAME, alarm.getAlarmName());

        getDatabase().update(ALARM_TABLE, cv, "_id=" + alarm.getId(), null);
    }
    public static void deleteEntry(Alarm alarm){
        deleteEntry(alarm.getId());
    }

    private static void deleteEntry(int id){
        getDatabase().delete(ALARM_TABLE, COLUMN_ALARM_ID + "=" + id, null);
    }

    public static void deleteAll(){
        getDatabase().delete(ALARM_TABLE, "1", null);
    }

    private static Cursor getCursor() {
        // TODO Auto-generated method stub
        String[] columns = new String[] {
                COLUMN_ALARM_ID,
                COLUMN_ALARM_ACTIVE,
                COLUMN_ALARM_TIME,
                COLUMN_ALARM_DAYS,
                COLUMN_ALARM_DIFFICULTY,
                COLUMN_ALARM_TONE,
                COLUMN_ALARM_VIBRATE,
                COLUMN_ALARM_NAME
        };
        return getDatabase().query(ALARM_TABLE, columns, null, null, null, null,
                null);
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALARM_TABLE + " ( "
                + COLUMN_ALARM_ID + " INTEGER primary key autoincrement, "
                + COLUMN_ALARM_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_ALARM_TIME + " TEXT NOT NULL, "
                + COLUMN_ALARM_DAYS + " BLOB NOT NULL, "
                + COLUMN_ALARM_DIFFICULTY + " INTEGER NOT NULL, "
                + COLUMN_ALARM_TONE + " TEXT NOT NULL, "
                + COLUMN_ALARM_VIBRATE + " INTEGER NOT NULL, "
                + COLUMN_ALARM_NAME + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
        onCreate(db);
    }

    public static List<Alarm> getAll() {
        List<Alarm> alarms = new ArrayList<>();
        Cursor cursor = Database.getCursor();
        if (cursor.moveToFirst()) {

            do {
                // COLUMN_ALARM_ID,
                // COLUMN_ALARM_ACTIVE,
                // COLUMN_ALARM_TIME,
                // COLUMN_ALARM_DAYS,
                // COLUMN_ALARM_DIFFICULTY,
                // COLUMN_ALARM_TONE,
                // COLUMN_ALARM_VIBRATE,
                // COLUMN_ALARM_NAME

                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setAlarmActive(cursor.getInt(1) == 1);
                alarm.setAlarmTime(cursor.getString(2));
                byte[] repeatDaysBytes = cursor.getBlob(3);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                        repeatDaysBytes);
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            byteArrayInputStream);
                    Alarm.Day[] repeatDays;
                    Object object = objectInputStream.readObject();
                    if (object instanceof Alarm.Day[]) {
                        repeatDays = (Alarm.Day[]) object;
                        alarm.setDays(repeatDays);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                alarm.setDifficulty(Alarm.Difficulty.values()[cursor.getInt(4)]);
                alarm.setAlarmTonePath(cursor.getString(5));
                alarm.setVibrate(cursor.getInt(6) == 1);
                alarm.setAlarmName(cursor.getString(7));

                alarms.add(alarm);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return alarms;
    }
}