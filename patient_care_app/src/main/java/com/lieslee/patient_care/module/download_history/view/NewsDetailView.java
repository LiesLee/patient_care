package com.lieslee.patient_care.module.download_history.view;

import com.common.base.ui.BaseView;
import com.lieslee.patient_care.bean.News;

/**
 * Created by LiesLee on 17/7/14.
 */

public interface NewsDetailView extends BaseView {
    void getNewsSuccessed(News news);
}
