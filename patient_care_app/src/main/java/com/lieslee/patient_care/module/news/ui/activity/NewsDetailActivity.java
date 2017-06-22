package com.lieslee.patient_care.module.news.ui.activity;

import android.view.View;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.utils.UIHelper;
import com.views.ProgressWebView;
import com.views.ProgressWheel;

import butterknife.Bind;

/**
 * Created by LiesLee on 2017/6/21.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.act_news_detail, toolbarTitle = R.string.news_detail)
public class NewsDetailActivity extends BaseActivity {
    @Bind(R.id.webview)
    ProgressWebView webview;
    @Bind(R.id.pw_loding)
    ProgressWheel pw_loding;


    @Override
    protected void initView() {
        UIHelper.initWebView(baseActivity, webview, pw_loding, "http://www.baidu.com");
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
