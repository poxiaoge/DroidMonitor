package com.ss.android.article.webmonitor;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by poxiaoge on 2017/1/18.
 */

public class Utils {

    public static String createSessionId() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String salt = dateFormat.format(date);
        UUID uuid = UUID.nameUUIDFromBytes(salt.getBytes());
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    //http://localhost:3000/add-key?sessionId=abc&monitorInterval=20000
// &goods=%E7%B4%A2%E5%B0%BCps4&startPrice=0&endPrice=3000
    public static String createAddKeyURLString(KeyItem keyItem) {
        String value2 = null;
        try {
            value2 = URLEncoder.encode(keyItem.getGoods(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str0 = BaseApplication.urlPrefix + "/add-key?sessionId=";
        String str1 = "&monitorInterval=";
        String str2 = "&goods=";
        String str3 = "&startPrice=";
        String str4 = "&endPrice=";

        String urlString = str0 + keyItem.getSessionId() + str1 + keyItem.getMonitorInterval()
                + str2 + value2 + str3 + keyItem.getStartPrice() + str4 + keyItem.getEndPrice();
        return urlString;
    }

    //http://localhost:3000/delete-key?sessionId=def
    public static String createDeleteKeyURLString(String sessionId) {
        return BaseApplication.urlPrefix + "/delete-key?sessionId=" + sessionId;
    }

    //    http://localhost:3000/query-key?sessionId=def
    public static String createQueryKeyURLString(String sessionId) {
        return BaseApplication.urlPrefix + "/query-key?sessionId=" + sessionId;
    }

    public static String createGetListURLString() {
        return BaseApplication.urlPrefix+"/get-key-list";
    }

    public static String createLocalURLString(String key, String start, String end) {
        String keyURL=null;
        try {
            keyURL = URLEncoder.encode(key, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "https://s.2.taobao.com/list/list.htm?spm=2007.1000337.6.2.HQdTi0&st_edtime=1&start="+start+"&end="+end+"&q="+keyURL+"&ist=0";
              //https://s.2.taobao.com/list/list.htm?spm=2007.1000337.6.2.HQdTi0&st_edtime=1&start=1500&end=3000&q=%CB%F7%C4%E1ps4&ist=0
    }

    public static byte[] read(InputStream inputStream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        inputStream.close();
        return out.toByteArray();
    }

    public static String getHttpData(String urlString) throws Exception {
        String data = null;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(6000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data_byte = read(inputStream);
            data = data_byte.toString();
        } else {
            data = "";
        }
        Log.e("getHttpData:", data);
        return data;
    }

    public static Boolean verifyGoodsEqual(GoodsItem goodsItem1, GoodsItem goodsItem2) {
        if (goodsItem1.getLink().equals(goodsItem2.getLink()) &&
                goodsItem1.getPrice().equals(goodsItem2.getPrice()) &&
                goodsItem2.getTitle().equals(goodsItem2.getTitle()) &&
                goodsItem1.getDesc().equals(goodsItem2.getDesc())
                ) {
            return true;
        } else {
            return false;
        }


    }


}
