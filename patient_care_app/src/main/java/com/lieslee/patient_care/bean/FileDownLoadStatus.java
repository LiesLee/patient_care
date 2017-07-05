package com.lieslee.patient_care.bean;

/**
 * Created by LiesLee on 2017/7/4.
 * Email: LiesLee@foxmail.com
 */

public class FileDownLoadStatus {
    /** 0:html、1:cover image、2：audio、3：video  */
    private int type;
    private String url;
    private boolean isDone;
    private float progress;


    public FileDownLoadStatus(int type, String url, boolean isDone) {
        this.type = type;
        this.url = url;
        this.isDone = isDone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
