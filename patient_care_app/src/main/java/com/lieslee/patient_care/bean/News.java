package com.lieslee.patient_care.bean;

/**
 * Created by LiesLee on 17/6/30.
 */

public class News {




    private Audio audio;
    private Video video;
    /**
     * id : 2
     * title : 不知道吧，几样日常凡物竟是控油奇兵，美容师绝不会告诉你的秘方
     * description : 五月的天气已经和以往的六月一模一样了，走路都能出一身汗，而且脸上总是会有一层油，不仅别人看起来不美观，自己也会觉得不舒服呢。
     * timestamp : 1498463687
     * update_time : 2017-06-26 15:54:47
     * cover_image : http://test.jsb-app.com/lenovo/uploads/20170626/14984636874.jpg
     * media_type : 1
     * html_download : http://test.jsb-app.com/lenovo/uploads//html/2.html
     * audio : {"id":3,"title":"这是个音频","remark":"这款面膜，不但有帮助美白，同时还能控油和收缩毛孔，特别是黄瓜的水分还能帮助保湿补水哟！","url":"http://test.jsb-app.com/lenovo/uploads/audio/14984640381.mp3"}
     * video : {"id":4,"title":"这是个视频","remark":"","url":"http://test.jsb-app.com/lenovo/uploads/video/11111.mp4"}
     */

    private Long id;
    private String title;
    private String description;
    private Long timestamp;
    private String update_time;
    private String cover_image;
    /** 1 video 、2 audio */
    private int media_type;
    private String html_download;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public String getHtml_download() {
        return html_download;
    }

    public void setHtml_download(String html_download) {
        this.html_download = html_download;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }


}
