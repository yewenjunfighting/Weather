package com.example.a17289.weather;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 17289 on 2019/7/8.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Handler handler;
    public AlarmReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent){
       Message msg = handler.obtainMessage();
       msg.what = 1;
       Log.d("receive", "something");
       handler.sendMessage(msg);
    }
}