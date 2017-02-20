package com.ss.android.article.webmonitor;

/**
 * Created by poxiaoge on 2017/1/18.
 */

public class GoodsItem {
    private String title;
    private String link;
    private String price;
    private String desc;
    private String seller;

    public GoodsItem(){
    }

    public GoodsItem(String title, String link, String price, String desc) {
        this.title = title;
        this.link = link;
        this.price = price;
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSeller() {
        return seller;
    }
}
