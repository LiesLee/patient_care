package com.lieslee.patient_care.module.download_history.view;

import com.common.base.ui.BaseView;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.bean.NewsListResponse;

import java.util.List;

/**
 * Created by LiesLee on 17/6/30.
 */

public interface NewsListView extends BaseView {
    void loadNewsListSuccessed(NewsListResponse data);
    void getNewsFromDBSuccessed(List<News> data);
    void getNotDownloadNews(List<News> data);
}
