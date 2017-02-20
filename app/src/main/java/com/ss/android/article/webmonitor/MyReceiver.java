package com.ss.android.article.webmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private final String tag = "MyReceiver";
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("MyReceiver", "restart dmr service……");
        Intent i = new Intent(context, WebService.class);
        context.startService(i);
    }
}
