package com.lieslee.patient_care.bean;

/**
 * news entity
 * Created by LiesLee on 17/6/30.
 */

public class News {
    /**
     * Illustrate：
     *
     * filename extension = url.substring(lastIndexOf(String str)+1)
     *
     * save folder path = {$sd card root} +/patient-care/news/{$new id}/
     *
     * cover_image file path = {$folder path} / {$new id}.jpg
     * audio file path = {$folder path} / {$audio id}.mp3
     * video file path = {$folder path} / {video id}.mp4
     * html_download file path = {$folder path} / {$new id}.html
     *
     */


    /** 0 is not download, 1 is being download, 2 complete  */
    private int download_status = 0;
    private float progress = 0.0f;


    private Audio audio;
    private Video video;

    private Long id;
    private String title;
    private String description;
    /** Sort by time */
    private Long timestamp;
    private String update_time;
    private String cover_image;
    /**
     * 1 video 、2 audio
     */
    private int media_type;
    private String html_download;

    public int getDownload_status() {
        return download_status;
    }

    public void setDownload_status(int download_status) {
        this.download_status = download_status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
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
