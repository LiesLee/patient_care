package com.lieslee.patient_care.module.download_history.presenter;

import com.common.base.presenter.BasePresenterImpl;
import com.common.callback.RequestCallback;
import com.common.utils.FastJsonUtil;
import com.lieslee.patient_care.bean.Audio;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.bean.NewsListResponse;
import com.lieslee.patient_care.bean.Video;
import com.lieslee.patient_care.common.Constant;
import com.lieslee.patient_care.dao.GreenDaoManager;
import com.lieslee.patient_care.dao.gen.AudioDao;
import com.lieslee.patient_care.dao.gen.NewsDao;
import com.lieslee.patient_care.dao.gen.VideoDao;
import com.lieslee.patient_care.http.HttpUtil;
import com.lieslee.patient_care.http.protocol.CommonProtocol;
import com.lieslee.patient_care.module.download_history.view.NewsListView;
import com.lieslee.patient_care.utils.UIHelper;
import com.socks.library.KLog;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by LiesLee on 17/6/30.
 */

public class NewsListPresenter extends BasePresenterImpl<NewsListView> {
    Long firstTime = -1L;

    public NewsListPresenter(NewsListView view) {
        super(view);
    }

    /**
     * when first load id=0, than the last
     *
     * @param update_time
     */
    public void loadNewsList(long update_time) {
        HttpUtil.requestDefault(CommonProtocol.downloadLists(update_time), mView, new RequestCallback<NewsListResponse>() {
            @Override
            public void beforeRequest() {
                mView.showProgress(Constant.PROGRESS_TYPE_DIALOG);
            }

            @Override
            public void requestError(int code, String msg) {
                if (mView == null) return;
                if (code == 0) {
                    mView.toast("网络失败异常,请稍后再试");
                } else {
                    mView.toast(msg);
                }
                mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
            }

            @Override
            public void requestComplete() {
                if (mView == null) return;
                mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
            }

            @Override
            public void requestSuccess(NewsListResponse data) {
                GreenDaoManager.getInstance().getNewSession().getNewsDao().insertOrReplaceInTx(data.getLists());
                GreenDaoManager.getInstance().getNewSession().getAudioDao().insertOrReplaceInTx(UIHelper.getAudios(data.getLists()));
                GreenDaoManager.getInstance().getNewSession().getVideoDao().insertOrReplaceInTx(UIHelper.getVideos(data.getLists()));
                mView.loadNewsListSuccessed(data);
            }
        });
    }

    public void getNewsFromDB(final int offset, final boolean isFirst) {
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<List<News>>() {
            @Override
            public void call(Subscriber<? super List<News>> subscriber) {
                List<News> list = null;
                if(isFirst){
                    list = GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder()
                            .orderDesc(NewsDao.Properties.Timestamp).offset(offset * Constant.DB_PAGE_SIZE).limit(Constant.DB_PAGE_SIZE).list();
                    if(list!=null && list.size() > 0){
                        firstTime = list.get(0).getTimestamp();
                    }
                }else{
                    if(firstTime > 0){
                        list = GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder().where(NewsDao.Properties.Timestamp.lt(firstTime))
                                .orderDesc(NewsDao.Properties.Timestamp).offset(offset * Constant.DB_PAGE_SIZE).limit(Constant.DB_PAGE_SIZE).list();
                    }else{
                        list = GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder()
                                .orderDesc(NewsDao.Properties.Timestamp).offset(offset * Constant.DB_PAGE_SIZE).limit(Constant.DB_PAGE_SIZE).list();
                    }
                }

                if(list!=null && list.size() > 0){
                    for(News news : list){
                        news.setIsInitStatus(false);
                        Video video = null;
                        Audio audio = null;
                        if(news!=null && news.getAudio_id() != null && news.getAudio_id() != 0L){
                            audio = GreenDaoManager.getInstance().getNewSession().getAudioDao().queryBuilder()
                                    .where(AudioDao.Properties.Id.eq(news.getAudio_id())).unique();
                            news.setAudio(audio);
                        }
                        if(news!=null && news.getVideo_id() != null && news.getVideo_id() != 0L){
                            video = GreenDaoManager.getInstance().getNewSession().getVideoDao().queryBuilder()
                                    .where(VideoDao.Properties.Id.eq(news.getVideo_id())).unique();
                            news.setVideo(video);
                        }

                    }
                }

                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mView != null && isFirst)
                            mView.showProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<News>>() {
                    @Override
                    public void onNext(List<News> data) {
                        if(data!=null && data.size() > 0) KLog.json(FastJsonUtil.t2Json2(data));
                        if (mView != null) {
                            mView.getNewsFromDBSuccessed(data);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView != null && isFirst)
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e);
                        if (mView != null) {
                            mView.getNewsFromDBSuccessed(null);
                            if (isFirst) mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                        }

                    }
                });
    }

    public void getAllNewsFromDB() {
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<List<News>>() {
            @Override
            public void call(Subscriber<? super List<News>> subscriber) {
                List<News> list = GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder()
                        .orderDesc(NewsDao.Properties.Timestamp).list();
                if(list!=null && list.size() > 0){
                    firstTime = list.get(0).getTimestamp();
                    for(News news : list){
                        news.setIsInitStatus(false);
                        Video video = null;
                        Audio audio = null;
                        if(news!=null && news.getAudio_id() != null && news.getAudio_id() != 0L){
                            audio = GreenDaoManager.getInstance().getNewSession().getAudioDao().queryBuilder()
                                    .where(AudioDao.Properties.Id.eq(news.getAudio_id())).unique();
                            news.setAudio(audio);
                        }
                        if(news!=null && news.getVideo_id() != null && news.getVideo_id() != 0L){
                            video = GreenDaoManager.getInstance().getNewSession().getVideoDao().queryBuilder()
                                    .where(VideoDao.Properties.Id.eq(news.getVideo_id())).unique();
                            news.setVideo(video);
                        }

                    }
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mView != null)
                            mView.showProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<News>>() {
                    @Override
                    public void onNext(List<News> data) {
                        if (mView != null) {
                            mView.getNewsFromDBSuccessed(data);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView != null)
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e);
                        if (mView != null) {
                            mView.getNewsFromDBSuccessed(null);
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                        }

                    }
                });
    }

    public void getNotDownloadNews() {
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<List<News>>() {
            @Override
            public void call(Subscriber<? super List<News>> subscriber) {
                subscriber.onNext(GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder()
                        .orderDesc(NewsDao.Properties.Timestamp).where(NewsDao.Properties.Download_status.eq(0)).list());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<List<News>>() {
                    @Override
                    public void onNext(List<News> data) {
                        if (mView != null) {
                            mView.getNotDownloadNews(data);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.getNotDownloadNews(null);
                        }
                    }
                });
    }

    public void updateNewsIsDownloaded(final News news) {
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                GreenDaoManager.getInstance().getNewSession().update(news);
                GreenDaoManager.getInstance().getNewSession().clear();// clear Identity Scope
                KLog.e(FastJsonUtil.t2Json2(news));
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String data) {

                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.getNotDownloadNews(null);
                        }
                    }
                });
    }

    public void deleteNews(final News news) {
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                GreenDaoManager.getInstance().getNewSession().delete(news);
                GreenDaoManager.getInstance().getNewSession().clear();// clear Identity Scope
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mView != null)
                            mView.showProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String data) {
                    }

                    @Override
                    public void onCompleted() {
                        if (mView != null)
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e);
                        if (mView != null) {
                            mView.getNewsFromDBSuccessed(null);
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                        }

                    }
                });
    }
}
