package com.ss.android.article.webmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ss.android.article.webmonitor.BaseApplication.INVALID_SESSION_ID;
import static com.ss.android.article.webmonitor.BaseApplication.NEW_GOODS;
import static com.ss.android.article.webmonitor.BaseApplication.QUERY_GOODS_ITEM_UNDEFINED;
import static com.ss.android.article.webmonitor.BaseApplication.QUERY_KEY_OK;

public class WebService extends Service {

    Context  mContext;
    private NotificationManager mNManager;
    Bitmap LargeBitmap = null;
    private static final int NOTIFYID_1 = 2;

    private PowerManager.WakeLock mWakeLock;
    PowerManager powerManager;
    WifiManager wifiManager;
    WifiManager.WifiLock wifiLock;




    private final String tag = this.getClass().getSimpleName();

    public WebService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        acquireWakeLock();
        LargeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon3);
        mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder localBuilder = new Notification.Builder(this);
        localBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        localBuilder.setAutoCancel(false);
        localBuilder.setSmallIcon(R.drawable.app_icon3);
        localBuilder.setTicker("Foreground Service Start");
        localBuilder.setContentTitle("MonitorServer服务端");
        localBuilder.setContentText("正在运行...");
        startForeground(1, localBuilder.getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (String sessionId : BaseApplication.getSessionIdList()) {
                    String urlString = Utils.createQueryKeyURLString(sessionId);
                    new HttpHandleThread("query", sessionId, urlString, myHandler).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        },100,BaseApplication.queryServerInterval);

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        Log.e(tag, "onDestroy");
        releaseWakeLock();
        stopForeground(true);
        Intent intent = new Intent("com.ss.android.article.webmonitor.action.start_service");
        sendBroadcast(intent);
        super.onDestroy();
    }

    private void acquireWakeLock() {
        if (mWakeLock == null) {
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            // wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
            // | PowerManager.ON_AFTER_RELEASE, this.getClass()
            // .getCanonicalName());
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
            mWakeLock.acquire();
        }
        if (wifiLock == null) {
            wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            wifiLock = wifiManager.createWifiLock(tag);
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
            wifiLock = null;
        }
    }






    public void pushNotification(String sessionId) {
        GoodsItem goodsItem = BaseApplication.getSessionItem(sessionId).getGoodsItem();

        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle(goodsItem.getTitle())                        //标题
                .setContentText(goodsItem.getDesc())      //内容
                .setSubText("点击打开")                    //内容下面的一小段文字
                .setTicker("闲鱼新发现")             //收到信息后状态栏显示的文字信息
                .setWhen(System.currentTimeMillis())           //设置通知时间
                .setSmallIcon(R.drawable.app_icon3)
                .setLargeIcon(LargeBitmap) //设置小图标//设置大图标
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)    //设置默认的三色灯与振动器 //设置自定义的提示音
                .setAutoCancel(true)                           //设置点击后取消Notification
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));                        //设置PendingIntent
        Notification notify1 = mBuilder.build();
        mNManager.notify(NOTIFYID_1, notify1);
    }


    public Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_KEY_OK:
//                    Toast.makeText(mContext,"QUERY_KEY_OK",Toast.LENGTH_SHORT).show();
                    break;
                case NEW_GOODS:
                    Bundle b = msg.getData();
                    String sessionId = b.getString("new");
                    pushNotification(sessionId);
                    Toast.makeText(mContext,"NEW_GOODS",Toast.LENGTH_SHORT).show();
                    break;
                case INVALID_SESSION_ID:
                    Toast.makeText(mContext,"INVALID_SESSION_ID",Toast.LENGTH_SHORT).show();
                    break;
                case QUERY_GOODS_ITEM_UNDEFINED:
                    Toast.makeText(mContext,"QUERY_GOODS_ITEM_UNDEFINED",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };





}
