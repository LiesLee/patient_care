package com.lieslee.patient_care.bean;

/**
 * Created by LiesLee on 17/6/30.
 */

public class Video {
    /**
     * id : 34
     * title : 测试视频
     * remark :
     * url : http://test.jsb-app.com/lenovo/uploads/video/14984641993.mp4
     */

    private Long id;
    private String title;
    private String remark;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
