package com.ss.android.article.webmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ss.android.article.webmonitor.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.ss.android.article.webmonitor.BaseApplication.ADD_KEY_OK;
import static com.ss.android.article.webmonitor.BaseApplication.DELETE_KEY_OK;
import static com.ss.android.article.webmonitor.BaseApplication.GET_HTTP_DATA_FAIL;
import static com.ss.android.article.webmonitor.BaseApplication.GET_LIST_OK;
import static com.ss.android.article.webmonitor.BaseApplication.INVALID_SESSION_ID;
import static com.ss.android.article.webmonitor.BaseApplication.MAX_SESSION_OVERFLOW;
import static com.ss.android.article.webmonitor.BaseApplication.MISS_VALUE;
import static com.ss.android.article.webmonitor.BaseApplication.NEW_GOODS;
import static com.ss.android.article.webmonitor.BaseApplication.QUERY_GOODS_ITEM_UNDEFINED;
import static com.ss.android.article.webmonitor.BaseApplication.QUERY_KEY_OK;
import static com.ss.android.article.webmonitor.Utils.read;

/**
 * Created by poxiaoge on 2017/1/18.
 */


public class HttpHandleThread extends Thread {

    String type;
    String urlString;
    Handler handler;
    String sessionId;

    private final String tag = this.getClass().getSimpleName();


    public HttpHandleThread(String type, String sessionId, String urlString, Handler handler) {
        this.urlString = urlString;
        this.handler = handler;
        this.type = type;
        this.sessionId = sessionId;
    }


    @Override
    public void run() {
        try {
            Log.e(tag, "type：" + type + " id: " + sessionId);
            String data = null;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 400) {
                InputStream inputStream = conn.getInputStream();
                byte[] data_byte = read(inputStream);
                data = new String(data_byte);
                Log.e("getHttpData:", data);
                switch (type) {
                    case "query":
                        if (responseCode == 200) {
                            GoodsItem goods = JSON.parseObject(data, GoodsItem.class);
//                            Log.e(tag, "new goods title is :" + goods.getTitle());
                            if (BaseApplication.getSessionItem(sessionId) != null) {
                                GoodsItem oldGoods = BaseApplication.getSessionItem(sessionId).getGoodsItem();
                                if (oldGoods != null) {
                                    String oldGoodsString = JSON.toJSONString(oldGoods);
//                                    Log.e(tag, "oldGoodString: " + oldGoodsString);
                                    if (Utils.verifyGoodsEqual(oldGoods, goods)) {
                                        handler.sendEmptyMessage(QUERY_KEY_OK);
                                    } else {
                                        BaseApplication.getSessionItem(sessionId).setGoodsItem(goods);
                                        Bundle b = new Bundle();
                                        b.putString("new", sessionId);
                                        Message msg = handler.obtainMessage();
                                        msg.setData(b);
                                        msg.what = NEW_GOODS;
                                        handler.sendMessage(msg);
                                    }
                                } else {
                                    BaseApplication.getSessionItem(sessionId).setGoodsItem(goods);
                                    Bundle b = new Bundle();
                                    b.putString("new", sessionId);
                                    Message msg = handler.obtainMessage();
                                    msg.setData(b);
                                    msg.what = NEW_GOODS;
                                    handler.sendMessage(msg);
                                }
                            }
                        } else {
                            Log.e(tag, "Invalid Query: " + responseCode);
                            if (data.equals("invalid-sessionId")) {
                                handler.sendEmptyMessage(INVALID_SESSION_ID);
                            }
                            if (data.equals("goods_item_undefined")) {
                                handler.sendEmptyMessage(QUERY_GOODS_ITEM_UNDEFINED);
                            }
                        }
                        break;
                    case "add":
                        if (responseCode == 200) {
                            //TODO：获取SessionItem时要进行非空判断！
                            handler.sendEmptyMessage(ADD_KEY_OK);
                        } else {
                            if (data.equals("max-session-overflow")) {
                                handler.sendEmptyMessage(MAX_SESSION_OVERFLOW);
                            }
                            if (data.equals("miss-value")) {
                                handler.sendEmptyMessage(MISS_VALUE);
                            }
                        }
                        break;
                    case "delete":
                        if (responseCode == 200) {
                            //TODO：进行query或delete操作前最好进行一个containsKey的判断！
                            BaseApplication.deleteSessionItem(sessionId);
                            handler.sendEmptyMessage(DELETE_KEY_OK);
                        }
                        break;
                    case "get_list":
                        if (responseCode == 200) {
                            List<SessionItem> sessionItems = JSON.parseArray(data,SessionItem.class);
                            BaseApplication.setSessionList(sessionItems);
                            handler.sendEmptyMessage(GET_LIST_OK);
                        }
                        break;
                }

            } else {
                //TODO：如果HTTP传输失败，data将会是""空字符串！
                data = "";
                handler.sendEmptyMessage(GET_HTTP_DATA_FAIL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(GET_HTTP_DATA_FAIL);
        }

    }
}
