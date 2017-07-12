package com.lieslee.patient_care.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by LiesLee on 17/6/30.
 */
@Entity
public class Audio {
    /**
     * id : 33
     * title : 测试音频
     * remark : 6月25日，大熊猫“健健”“康康”在生日庆祝会上。
     * url : http://test.jsb-app.com/lenovo/uploads/audio/14984640381.mp3
     */
    @Id
    private Long id;
    private String title;
    private String remark;
    private String url;

    @Generated(hash = 408350950)
    public Audio(Long id, String title, String remark, String url) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.url = url;
    }

    @Generated(hash = 1642629471)
    public Audio() {
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
