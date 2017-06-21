package com.lieslee.patient_care.module.download_history.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseFragment;
import com.lai.library.ButtonStyle;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.module.download_history.ui.adapter.NewsAdapter;

import java.util.Arrays;

import butterknife.Bind;

/**
 * Created by LiesLee on 2017/6/19.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.fra_news)
public class DownLoadManagerHistoryFragment extends BaseFragment{
    @Bind(R.id.ll_pull)
    LinearLayout ll_pull;

    @Bind(R.id.bts_pull)
    ButtonStyle bts_pull;

    @Bind(R.id.rv_list)
    RecyclerView rv_list;

    NewsAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void initView(View fragmentRootView) {
        mAdapter = new NewsAdapter(baseActivity, R.layout.item_news, Arrays.asList(new String[10]));
        mLinearLayoutManager = new LinearLayoutManager(baseActivity);
        rv_list.setLayoutManager(mLinearLayoutManager);
        rv_list.setAdapter(mAdapter);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
