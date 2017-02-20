package com.ss.android.article.webmonitor;

/**
 * Created by poxiaoge on 2017/1/19.
 */

public class KeyItem {
    private String sessionId;
    private String monitorInterval;
    private String goods;
    private String startPrice;
    private String endPrice;

    public void setEndPrice(String endPrice) {
        this.endPrice = endPrice;
    }

    public String getEndPrice() {
        return endPrice;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getGoods() {
        return goods;
    }

    public void setMonitorInterval(String monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public String getMonitorInterval() {
        return monitorInterval;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getStartPrice() {
        return startPrice;
    }


}
