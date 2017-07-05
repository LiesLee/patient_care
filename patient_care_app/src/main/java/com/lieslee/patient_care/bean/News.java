package com.lieslee.patient_care.bean;

import android.content.Context;
import android.text.TextUtils;

import com.lieslee.patient_care.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private List<FileDownLoadStatus> statuses;
    private boolean isInitStatus = false;

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

    public String getFileNameExtension(String url){
        if (TextUtils.isEmpty(url)) return "";
        return url.substring(url.lastIndexOf("."));
    }

    public String getPath(Context context){
        String sdDir = FileUtils.getPath(context, false);
        File file = new File(sdDir + "news/" +id);
        if(!file.exists()){
            file.mkdir();
        }
        return sdDir +id;
    }

    public String getCoverImagePath(Context context){
        String path = "";
        if(TextUtils.isEmpty(cover_image)) return path;
        return getPath(context)+"/"+ id + getFileNameExtension(cover_image);
    }


    public String getVideoPath(Context context){
        String path = "";
        if(video == null) return path;
        return getPath(context)+"/"+ video.getId() + getFileNameExtension(video.getUrl());
    }

    public String getAudioPath(Context context){
        String path = "";
        if(audio == null) return path;
        return getPath(context)+"/"+ audio.getId() + getFileNameExtension(audio.getUrl());
    }

    public String getHtmlPath(Context context){
        String path = "";
        if(TextUtils.isEmpty(html_download)) return path;
        return getPath(context)+"/"+ id + getFileNameExtension(html_download);
    }

    public void initFileDownloadStatus(){
        if(!isInitStatus){
            if(statuses == null) statuses = new ArrayList<>();
            statuses.add(new FileDownLoadStatus(0, html_download, false));
            if(!TextUtils.isEmpty(cover_image)) statuses.add(new FileDownLoadStatus(1,cover_image,false));
            if (audio != null) statuses.add(new FileDownLoadStatus(2,audio.getUrl(), false));
            if (video!=null) statuses.add(new FileDownLoadStatus(3,video.getUrl(), false));
            isInitStatus = true;
        }
    }

    public List<FileDownLoadStatus> getFileDownLoadStatus(){
        if(!isInitStatus) initFileDownloadStatus();
        return statuses;
    }

    public float getNowProgress(){
        float progress = 0.0f;
        for(FileDownLoadStatus status : getFileDownLoadStatus()){
            if(status.isDone()){
                progress = progress+100;
            }else{
                progress = progress + status.getProgress();
            }
        }
        return progress;
    }


}
