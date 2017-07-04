package com.lieslee.patient_care.module.download_history.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.common.annotation.ActivityFragmentInject;
import com.common.base.presenter.BasePresenterImpl;
import com.common.base.ui.BaseFragment;
import com.lai.library.ButtonStyle;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.bean.NewsListResponse;
import com.lieslee.patient_care.module.download_history.presenter.NewsListPresenter;
import com.lieslee.patient_care.module.download_history.ui.adapter.NewsAdapter;
import com.lieslee.patient_care.module.download_history.view.NewsListView;
import com.socks.library.KLog;

import java.util.Arrays;

import butterknife.Bind;

/**
 * Created by LiesLee on 2017/6/19.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.fra_news)
public class NewsListFragment extends BaseFragment<NewsListPresenter> implements NewsListView {
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
        mPresenter = new NewsListPresenter(this);
        mAdapter = new NewsAdapter(baseActivity, R.layout.item_news, null);
        mLinearLayoutManager = new LinearLayoutManager(baseActivity);
        rv_list.setLayoutManager(mLinearLayoutManager);
        rv_list.setAdapter(mAdapter);

        bts_pull.setOnClickListener(this);

        findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }

    @Override
    public void initData() {
        mPresenter.loadNewsList(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bts_pull :
                mPresenter.loadNewsList(0);
                break;

            default:
                break;
        }
    }

    @Override
    public void loadNewsListSuccessed(NewsListResponse data) {
        mAdapter.setData(data.getLists());
    }

    public void startDownload(){
        News news = mAdapter.getData().get(1);


        if(!TextUtils.isEmpty(news.getCoverImagePath(baseActivity))){
            Aria.download(this)
                    .load(news.getCover_image())
                    .setDownloadPath(news.getCoverImagePath(baseActivity))	//文件保存路径
                    .add();
        }

        if(!TextUtils.isEmpty(news.getAudioPath(baseActivity))){
            Aria.download(this)
                    .load(news.getAudio().getUrl())
                    .setDownloadPath(news.getAudioPath(baseActivity))	//文件保存路径
                    .add();
        }
        if(!TextUtils.isEmpty(news.getVideoPath(baseActivity))){
            Aria.download(this)
                    .load(news.getVideo().getUrl())
                    .setDownloadPath(news.getVideoPath(baseActivity))	//文件保存路径
                    .add();
        }


        Aria.download(this)
                .load(news.getHtml_download())
                .setDownloadPath(news.getHtmlPath(baseActivity))	//文件保存路径
                .start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Aria.download(this).register();
    }

    @Download.onPre void onPre(DownloadTask task) {
        KLog.d("download onPre");
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        KLog.d("download start");
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        KLog.d("download resume");
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        updateState(task.getDownloadEntity());
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        updateState(task.getDownloadEntity());
    }

    void updateState(DownloadEntity item){
        String str = "";
        float progress = 0f;
        switch (item.getState()) {
            case DownloadEntity.STATE_WAIT:
            case DownloadEntity.STATE_OTHER:
            case DownloadEntity.STATE_FAIL:
                str = "开始";
                break;
            case DownloadEntity.STATE_STOP:
                str = "暂停";
                break;
            case DownloadEntity.STATE_PRE:
            case DownloadEntity.STATE_POST_PRE:
            case DownloadEntity.STATE_RUNNING:
                str = "下载中...";
                break;
            case DownloadEntity.STATE_COMPLETE:
                str = "下载完成";
                break;
        }

        progress =  item.getFileSize() == 0 || item.getCurrentProgress() == 0 ? 0 : (item.getCurrentProgress() * 100 / item.getFileSize());
        KLog.e("====================================");
        KLog.d(str);
        KLog.d(item.getDownloadUrl());
        KLog.d(progress+"%");
        KLog.e("====================================");
    }

}
