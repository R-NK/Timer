package com.alone.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public TextView textViewTime;
    public NumberPicker numberPickerMinutes;
    public NumberPicker numberPickerSeconds;
    public TextView textViewStart;
    public TextView textViewReset;
    public CountDown countDown;
    public long CurrentTime = 0;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManagerCompat nManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private ListView recentList;
    //private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTime = (TextView)findViewById(R.id.TimeText);
        numberPickerMinutes = (NumberPicker)findViewById(R.id.numberPickerMinutes);
        numberPickerSeconds = (NumberPicker)findViewById(R.id.numberPickerSeconds);
        textViewStart = (TextView)findViewById(R.id.StartText);
        textViewReset = (TextView)findViewById(R.id.ResetText);
        recentList = (ListView)findViewById(R.id.recentList);

        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setMaxValue(59);

        builder = new NotificationCompat.Builder(getApplicationContext());
        nManager = (NotificationManagerCompat.from(getApplicationContext()));
        intent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,R.layout.list,items);

    }

    public void OnClick_Reset(View view){
        numberPickerMinutes.setValue(0);
        numberPickerSeconds.setValue(0);
        if(CurrentTime == 0){
            return;
        }
        countDown.cancel();
        textViewStart.setText("START");
        textViewTime.setText("00:00");
        CurrentTime = 0;
        nManager.cancel(1);
    }

    public void OnClick_Start(View view){
        if (numberPickerMinutes.getValue() == 0 && numberPickerSeconds.getValue() == 0){
            return;
        } else {
            if(textViewStart.getText().equals("START")) {
                if(textViewTime.getText().equals("00:00")) {
                    countDown = new CountDown(numberPickerMinutes.getValue() * 60000 + numberPickerSeconds.getValue() * 1000 + 1000, 100);
                    countDown.start();
                    textViewStart.setText("STOP");
                    //履歴に追加
                    int time = numberPickerMinutes.getValue() * 60000 + numberPickerSeconds.getValue() * 1000;
                    long mm = time / 1000 / 60;
                    long ss = time / 1000 % 60;
                    String time_string = String.format("%1$02d:%2$02d", mm, ss);
                    Collections.reverse(items);
                    items.add(time_string);
                    Collections.reverse(items);
                    recentList.setAdapter(adapter);

                } else {
                    countDown = new CountDown(CurrentTime, 100);
                    countDown.start();
                    textViewStart.setText("STOP");
                }
            } else if(textViewStart.getText().equals("STOP")) {
                countDown.cancel();
                textViewStart.setText("START");
            }
        }
    }

    private void updateNotification(String time) {
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Timer");
        builder.setContentText(time);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        //notification.flags = Notification.FLAG_NO_CLEAR;    //    通知をスワイプや、全消去しても消えない
        nManager.notify(1, notification);
    }

    class CountDown extends CountDownTimer {
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            // 完了
            textViewTime.setText("00:00");
            textViewStart.setText("START");
            CurrentTime = 0;
        }
        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            long mm = millisUntilFinished / 1000 / 60;
            long ss = millisUntilFinished / 1000 % 60;
            CurrentTime = millisUntilFinished;
            String TimeString = String.format("%1$02d:%2$02d", mm, ss);
            textViewTime.setText(TimeString);

            updateNotification(TimeString);
        }
    }
}
