package com.lieslee.patient_care.bean;

import android.content.Context;
import android.text.TextUtils;

import com.lieslee.patient_care.utils.FileUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.lieslee.patient_care.dao.gen.DaoSession;
import com.lieslee.patient_care.dao.gen.VideoDao;
import com.lieslee.patient_care.dao.gen.AudioDao;
import com.lieslee.patient_care.dao.gen.NewsDao;

/**
 * news entity
 * Created by LiesLee on 17/6/30.
 */
@Entity
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


    /** -1 is download failed, 0 is not download, 1 is being download, 2 complete  */
    private int download_status = 0;
    private float progress = 0.0f;
    @Transient
    private List<FileDownLoadStatus> statuses;
    @Transient
    private boolean isInitStatus = false;
    @ToOne(joinProperty = "audio_id")
    private Audio audio;
    @ToOne(joinProperty = "video_id")
    private Video video;
    @Id
    private Long id;
    private Long video_id;
    private Long audio_id;
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
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 255022283)
    private transient NewsDao myDao;
    @Generated(hash = 330701068)
    public News(int download_status, float progress, Long id, Long video_id, Long audio_id, String title, String description,
            Long timestamp, String update_time, String cover_image, int media_type, String html_download) {
        this.download_status = download_status;
        this.progress = progress;
        this.id = id;
        this.video_id = video_id;
        this.audio_id = audio_id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.update_time = update_time;
        this.cover_image = cover_image;
        this.media_type = media_type;
        this.html_download = html_download;
    }
    @Generated(hash = 1579685679)
    public News() {
    }

    @Generated(hash = 1490908319)
    private transient Long audio__resolvedKey;
    @Generated(hash = 1581726105)
    private transient Long video__resolvedKey;
    /** -1 is download failed, 0 is not download, 1 is being download, 2 complete  */
    public int getDownload_status() {
        return download_status;
    }
    /** -1 is download failed, 0 is not download, 1 is being download, 2 complete  */
    public void setDownload_status(int download_status) {
        this.download_status = download_status;
    }

    public float getProgress() {
        return progress;
    }

    public Long getVideo_id() {
        return video_id;
    }

    public void setVideo_id(Long video_id) {
        this.video_id = video_id;
    }

    public Long getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(Long audio_id) {
        this.audio_id = audio_id;
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
    @Keep
    public Audio getAudio() {
        return audio;
    }
    @Keep
    public void setAudio(Audio audio) {
        this.audio = audio;
    }
    @Keep
    public Video getVideo() {
        return video;
    }
    @Keep
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
        if(video == null || TextUtils.isEmpty(video.getUrl())) return path;
        return getPath(context)+"/"+ video.getId() + getFileNameExtension(video.getUrl());
    }

    public String getAudioPath(Context context){
        String path = "";
        if(audio == null || TextUtils.isEmpty(audio.getUrl())) return path;
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
            if(!TextUtils.isEmpty(html_download)) statuses.add(new FileDownLoadStatus(0, html_download, false));
            if(!TextUtils.isEmpty(cover_image)) statuses.add(new FileDownLoadStatus(1,cover_image,false));
            if (audio != null && !TextUtils.isEmpty(audio.getUrl())) statuses.add(new FileDownLoadStatus(2,audio.getUrl(), false));
            if (video!=null && !TextUtils.isEmpty(video.getUrl())) statuses.add(new FileDownLoadStatus(3,video.getUrl(), false));
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
                progress = progress + 100;
            }else{
                progress = progress + status.getProgress();
            }
        }
        return progress;
    }
    public boolean getIsInitStatus() {
        return this.isInitStatus;
    }
    public void setIsInitStatus(boolean isInitStatus) {
        this.isInitStatus = isInitStatus;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 543991306)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNewsDao() : null;
    }


}
