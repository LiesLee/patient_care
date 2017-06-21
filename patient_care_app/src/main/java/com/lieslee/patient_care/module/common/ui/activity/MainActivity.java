package com.lieslee.patient_care.module.common.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.common.base.ui.BaseFragment;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.module.common.ui.fragment.WebViewFragment;
import com.lieslee.patient_care.module.download_history.ui.adapter.MainFragmentAdapter;
import com.lieslee.patient_care.module.download_history.ui.fragment.DownLoadManagerHistoryFragment;
import com.views.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

@ActivityFragmentInject(contentViewId = R.layout.act_main, toolbarTitle = R.string.app_name)
public class MainActivity extends BaseActivity {

    @Bind(R.id.vp_main)
    NonSwipeableViewPager vp_main;
    @Bind(R.id.tv_suanfufa)
    TextView tv_suanfufa;
    @Bind(R.id.tv_news)
    TextView tv_news;

    DownLoadManagerHistoryFragment newsFragment;
    WebViewFragment webViewFragment;
    List<BaseFragment> fragments;
    private MainFragmentAdapter fragmentAdapter;


    @Override
    protected void initView() {
        fragments = new ArrayList<>();
        newsFragment = new DownLoadManagerHistoryFragment();
        webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", "file:///android_asset/dist/index.html");
        webViewFragment.setArguments(bundle);
        fragments.add(newsFragment);
        fragments.add(webViewFragment);

        fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), fragments);
        vp_main.setAdapter(fragmentAdapter);
        vp_main.setOffscreenPageLimit(fragmentAdapter.getCount());

        tv_news.setOnClickListener(this);
        tv_suanfufa.setOnClickListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_news :
                vp_main.setCurrentItem(0);
                break;
            case R.id.tv_suanfufa :
                vp_main.setCurrentItem(1);
                break;

            default:
                break;
        }
    }
}
