package com.lieslee.patient_care.event;

import com.lieslee.patient_care.bean.News;

/**
 * Created by LiesLee on 17/7/13.
 */

public class ME_NewsSave {
    public News news;

    public ME_NewsSave(News news) {
        this.news = news;
    }
}
