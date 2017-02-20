package com.ss.android.article.webmonitor;

import android.app.Application;
import android.app.Service;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by poxiaoge on 2017/1/17.
 */

public class BaseApplication extends Application {

    public static String urlPrefix = "http://192.168.2.163:3000";
    //    public static String urlPrefix = "http://118.89.216.213:3000";
    public final static int GET_HTTP_DATA_OK = 0;
    public final static int GET_HTTP_DATA_FAIL = 1;
    public final static int ADD_KEY_OK = 2;
    public final static int ADD_KEY_FAIL = 3;
    public final static int DELETE_KEY_OK = 4;
    public final static int DELETE_KEY_FAIL = 5;
    public final static int QUERY_KEY_OK = 6;
    public final static int QUERY_KEY_FAIL = 7;
    public final static int INVALID_SESSION_ID = 8;
    public final static int QUERY_GOODS_ITEM_UNDEFINED = 9;
    public final static int MAX_SESSION_OVERFLOW = 10;
    public final static int MISS_VALUE = 11;
    public final static int NEW_GOODS = 12;
    public final static int GET_LIST_OK = 13;

    public static int queryServerInterval = 15000;

    public static List<SessionItem> sessionList = new ArrayList<>();


    public static List<String> getSessionIdList() {
        List<String> sessionIdList = new ArrayList<>();
        for (SessionItem sessionItem : sessionList) {
            sessionIdList.add(sessionItem.getSessionId());
        }
        return sessionIdList;
    }

    public static List<GoodsItem> getGoodsItemList() {
        List<GoodsItem> goodsItemList = new ArrayList<>();
        for (SessionItem sessionItem : sessionList) {
            goodsItemList.add(sessionItem.getGoodsItem());
        }
        return goodsItemList;
    }

    public static List<KeyItem> getKeyItemList() {
        List<KeyItem> keyItemList = new ArrayList<>();
        for (SessionItem sessionItem : sessionList) {
            keyItemList.add(sessionItem.getKeyItem());
        }
        Log.e("BaseApplication", "getKeyItemList:" + keyItemList.size());
        return keyItemList;
    }

    public static void deleteSessionItem(String sessionId) {
        synchronized (sessionList) {
            for (SessionItem sessionItem : sessionList) {
                if (sessionItem.getSessionId().equals(sessionId)) {
                    sessionList.remove(sessionItem);
                }
            }
        }
    }

    public static SessionItem getSessionItem(String sessionId) {
        for (SessionItem sessionItem : sessionList) {
            if (sessionItem.getSessionId().equals(sessionId)) {
                return sessionItem;
            }
        }
        return null;
    }

    public static void setSessionList(List<SessionItem> sessionItems) {
        //Arrays.asList(someclass [])可以将array转成list
        sessionList.clear();
        sessionList.addAll(sessionItems);
    }


//    public Handler myHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case GET_LIST_OK:
//                    Toast.makeText(getApplicationContext(),"GET_LIST_OK",Toast.LENGTH_SHORT).show();
//            }
//        }
//    };


}
