package com.rinat678.alarm;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmAlertActivity extends Activity{

    private Alarm alarm;

    private MediaPlayer mediaPlayer;

    private Vibrator vibrator;

    private boolean alarmActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm_alert);

        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");

        assert alarm != null;
        this.setTitle(alarm.getAlarmName());

        TextView text = (TextView) findViewById(R.id.text_wake_up);
        text.setText(getResources().getText(R.string.alarm_alert_text));

        SeekBar sb = (SeekBar) findViewById(R.id.mySeek);
        sb.setProgress(15);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int counts = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(15);
                //noinspection deprecation
                seekBar.setThumb(getResources().getDrawable(R.drawable.ic_fiber));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 50)
                    //noinspection deprecation
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_radio_button_unchecked_green));
                if (progress >= 85) {
                    seekBar.setProgress(85);
                    //noinspection deprecation
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_radio_button_unchecked_black_24dp));
                    if (counts++ == 0)
                        Toast.makeText(getApplicationContext(), "Будильник выключен", Toast.LENGTH_SHORT).show();
                        AlarmAlertActivity.super.onBackPressed();
                } else if (progress < 15) {
                    seekBar.setProgress(15);
                }
            }
        });


        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: "
                                + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException ignored) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException ignored) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        startAlarm();

    }

    @Override
    protected void onResume() {
        Log.d(getClass().getSimpleName(), "onResume");
        super.onResume();
        alarmActive = true;
    }

    private void startAlarm() {
        Log.d(getClass().getSimpleName(), "startAlarm");

        if (!alarm.getAlarmTonePath().equals("")) {
            mediaPlayer = new MediaPlayer();
            if (alarm.isVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {200, 200, 200, 200, 200, 200, 200, 300, 300, 300, 300, 700, 700, 700, 1000, 200, 200, 200, 200, 70, 70};
                vibrator.vibrate(pattern, 1);
            }
            try {
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this,
                        Uri.parse(alarm.getAlarmTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                mediaPlayer.release();
                alarmActive = false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (!alarmActive)
            super.onBackPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        super.onPause();
        StaticWakeLock.INSTANCE.lockOff(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy");
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception ignored) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception ignored) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception ignored) {

        }
        super.onDestroy();
    }
}
