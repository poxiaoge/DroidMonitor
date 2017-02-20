package com.ss.android.article.webmonitor;

/**
 * Created by poxiaoge on 2017/1/19.
 */

public class SessionItem {

    private String sessionId;
    private GoodsItem goodsItem;
    private KeyItem keyItem;
    private String childPid;

    public void setGoodsItem(GoodsItem goodsItem) {
        this.goodsItem = goodsItem;
    }

    public GoodsItem getGoodsItem() {
        return goodsItem;
    }

    public void setKeyItem(KeyItem keyItem) {
        this.keyItem = keyItem;
    }

    public KeyItem getKeyItem() {
        return keyItem;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setChildPid(String childPid) {
        this.childPid = childPid;
    }

    public String getChildPid() {
        return childPid;
    }
}
