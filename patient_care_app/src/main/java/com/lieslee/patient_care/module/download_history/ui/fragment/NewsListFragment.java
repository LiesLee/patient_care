package com.lieslee.patient_care.module.download_history.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseFragment;
import com.common.utils.FastJsonUtil;
import com.lai.library.ButtonStyle;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.bean.NewsListResponse;
import com.lieslee.patient_care.dao.GreenDaoManager;
import com.lieslee.patient_care.dao.gen.NewsDao;
import com.lieslee.patient_care.event.ME_NewsDelete;
import com.lieslee.patient_care.event.ME_NewsSave;
import com.lieslee.patient_care.event.ME_RedownloadNews;
import com.lieslee.patient_care.event.ME_StartDownLoad;
import com.lieslee.patient_care.module.download_history.presenter.NewsListPresenter;
import com.lieslee.patient_care.module.download_history.ui.adapter.NewsAdapter;
import com.lieslee.patient_care.module.download_history.view.NewsListView;
import com.lieslee.patient_care.utils.DialogHelper;
import com.lieslee.patient_care.utils.UIHelper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    int page = 1;
    int offset = 0;

    @Override
    protected void initView(View fragmentRootView) {
        FileDownloader.setup(baseActivity);
        mPresenter = new NewsListPresenter(this);
        mAdapter = new NewsAdapter(baseActivity, R.layout.item_news, null);
        mLinearLayoutManager = new LinearLayoutManager(baseActivity);
        rv_list.setLayoutManager(mLinearLayoutManager);
        rv_list.setAdapter(mAdapter);

        bts_pull.setOnClickListener(this);

        findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void initData() {
        page = 1;
        offset = 0;
        mPresenter.getNotDownloadNews(); //check has not download news
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bts_pull :
                if(page == 1){
                    if(mAdapter!=null && mAdapter.getData()!=null && mAdapter.getData().size() > 0){
                        long theLastTime = UIHelper.getLastTimeOnList(mAdapter.getData());
                        mPresenter.loadNewsList(theLastTime);
                    }else{
                        mPresenter.loadNewsList(0);
                    }

                }else{
                    ++page;
                    long theLastTime = UIHelper.getLastTimeOnList(mAdapter.getData());
                    mPresenter.loadNewsList(theLastTime);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void getNotDownloadNews(List<News> data) {
        if(data != null && data.size() > 0){ //has not download news
            ll_pull.setVisibility(View.GONE);
            mPresenter.getAllNewsFromDB();
        }else{
            mPresenter.getNewsFromDB(offset, true);
            ll_pull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadNewsListSuccessed(NewsListResponse data) {
        if(data!=null && data.getLists().size() > 0){
            ll_pull.setVisibility(View.GONE);
        }else{
            DialogHelper.showTipsDialog(baseActivity, "暂时没有更多资讯哦 ~ ", "好的", null);
            ll_pull.setVisibility(View.VISIBLE);
        }

        if(page == 1){
            if(data!=null && data.getLists().size() > 0){
                UIHelper.sortNews(data.getLists()); //sortting
                if(mAdapter.getData()!=null && mAdapter.getData().size() > 0){
                    data.getLists().addAll(mAdapter.getData());
                    mAdapter.getData().clear();
                    mAdapter.setData(data.getLists());
                    rv_list.smoothScrollToPosition(mAdapter.getFinalPositionOnAdapter(0));
                }else{
                    mAdapter.setData(data.getLists());
                }
            }

        }else{
            if(data!=null && data.getLists().size() > 0){
                data.getLists().addAll(mAdapter.getData());
                UIHelper.sortNews(data.getLists()); //sortting
                mAdapter.getData().clear();
                mAdapter.addNewData(data.getLists());
                rv_list.smoothScrollToPosition(mAdapter.getFinalPositionOnAdapter(0));
            }else{
                --page;
                if(page < 1) page = 1;
            }
        }

        //post notice to download
        EventBus.getDefault().post(new ME_StartDownLoad(0));

    }

    @Override
    public void getNewsFromDBSuccessed(final List<News> data) {
        if(offset == 0){
            mAdapter.setData(data);
            if(mAdapter.getData()!=null && mAdapter.getData().size() > 4){
                mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        ++offset;
                        mPresenter.getNewsFromDB(offset, false);
                    }
                });
            }
        }else{
            if(data==null || data.size() == 0){
                --offset;
                if(offset < 0) offset = 0;
            }
            rv_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addNewData(data);
                }
            }, 1500);

        }
        //post notice to download
        EventBus.getDefault().post(new ME_StartDownLoad(0));

    }



    public void startDownload(){
        News news = mAdapter.getNewsById(mAdapter.downloading_id);
        FileDownloadListener queueTarget = new FileDownloadListener() {
            long time = 0L;
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {}
            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {}
            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                long now = System.currentTimeMillis();
                if(now - time > 500){
                    time = now;
                    if(mAdapter!=null) mAdapter.updateProgress(task, soFarBytes, totalBytes);
                }
            }
            @Override
            protected void blockComplete(BaseDownloadTask task) {}
            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {}
            @Override
            protected void completed(BaseDownloadTask task) {
                if(mAdapter!=null) mAdapter.upDateItem(task, 0);
            }
            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {}
            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                KLog.e(e);
                if(mAdapter!=null) mAdapter.upDateItem(task, 1);
            }
            @Override
            protected void warn(BaseDownloadTask task) {}
        };

        FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);
        //队列任务设置不调用progress
        //queueSet.disableCallbackProgressTimes();
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
        List<BaseDownloadTask> tasks = new ArrayList<>();

        if(!TextUtils.isEmpty(news.getHtmlPath(baseActivity))){
            tasks.add(FileDownloader.getImpl()
                    .create(news.getHtml_download())
                    .setTag(news.getId())
                    .setPath(news.getHtmlPath(baseActivity)));
        }
        if(!TextUtils.isEmpty(news.getCoverImagePath(baseActivity))){
            tasks.add(FileDownloader.getImpl()
                    .create(news.getCover_image())
                    .setTag(news.getId())
                    .setPath(news.getCoverImagePath(baseActivity)));
        }
        if(!TextUtils.isEmpty(news.getAudioPath(baseActivity))){
            tasks.add(FileDownloader.getImpl()
                    .create(news.getAudio().getUrl())
                    .setTag(news.getId())
                    .setPath(news.getAudioPath(baseActivity)));
        }
        if(!TextUtils.isEmpty(news.getVideoPath(baseActivity))){
            tasks.add(FileDownloader.getImpl()
                    .create(news.getVideo().getUrl())
                    .setTag(news.getId())
                    .setPath(news.getVideoPath(baseActivity)));
        }
        if(tasks.size() > 0){ //有下载任务
            //并行下载
            //queueSet.downloadTogether(tasks);
            //串行下载（一个一个来）
            queueSet.downloadSequentially(tasks);
            news.setDownload_status(1);
            mAdapter.notifyDataSetChanged();
            queueSet.start();
        }else{ //无下载任务
            news.setDownload_status(2);
            mAdapter.notifyDataSetChanged();
        }

    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ME_StartDownLoad event) {
        if(event.type == 0){
            if(mAdapter.findDownloadItem()){ // 开始下载
                ll_pull.setVisibility(View.GONE);
                startDownload();
            }else{
                KLog.e("=======全部下载完成=======");
                ll_pull.setVisibility(View.VISIBLE);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ME_NewsSave event) {
        if(event.news != null && mPresenter != null){
            mPresenter.updateNewsIsDownloaded(event.news);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ME_NewsDelete event) {
        if(event.news != null && mPresenter != null){
            mPresenter.deleteNews(event.news);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ME_RedownloadNews event) {
        KLog.e("重新下载");
    }

}
