package com.lieslee.patient_care.bean;

/**
 * Created by LiesLee on 17/6/30.
 */

public class Audio {
    /**
     * id : 33
     * title : 测试音频
     * remark : 6月25日，大熊猫“健健”“康康”在生日庆祝会上。
     * url : http://test.jsb-app.com/lenovo/uploads/audio/14984640381.mp3
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
