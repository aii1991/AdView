package com.jasonzhang.library.bean;

public class AdBean {
    private int id;
    private String advPic; //广告图片地址
    private String advUrl; //广告跳转URL

    public AdBean(String advPic) {
        this.advPic = advPic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdvPic() {
        return advPic;
    }

    public void setAdvPic(String advPic) {
        this.advPic = advPic;
    }

    public String getAdvUrl() {
        return advUrl;
    }

    public void setAdvUrl(String advUrl) {
        this.advUrl = advUrl;
    }


}