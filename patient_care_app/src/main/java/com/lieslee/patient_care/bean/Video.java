package com.lieslee.patient_care.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by LiesLee on 17/6/30.
 */
@Entity
public class Video {
    /**
     * id : 34
     * title : 测试视频
     * remark :
     * url : http://test.jsb-app.com/lenovo/uploads/video/14984641993.mp4
     */
    @Id
    private Long id;
    private String title;
    private String remark;
    private String url;

    @Generated(hash = 552702196)
    public Video(Long id, String title, String remark, String url) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.url = url;
    }

    @Generated(hash = 237528154)
    public Video() {
    }

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
