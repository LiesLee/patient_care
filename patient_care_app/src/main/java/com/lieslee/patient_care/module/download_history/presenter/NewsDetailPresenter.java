package com.lieslee.patient_care.module.download_history.presenter;

import com.common.base.presenter.BasePresenterImpl;
import com.lieslee.patient_care.bean.Audio;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.bean.Video;
import com.lieslee.patient_care.common.Constant;
import com.lieslee.patient_care.dao.GreenDaoManager;
import com.lieslee.patient_care.dao.gen.AudioDao;
import com.lieslee.patient_care.dao.gen.NewsDao;
import com.lieslee.patient_care.dao.gen.VideoDao;
import com.lieslee.patient_care.module.download_history.view.NewsDetailView;
import com.socks.library.KLog;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by LiesLee on 17/7/14.
 */

public class NewsDetailPresenter extends BasePresenterImpl<NewsDetailView> {

    public NewsDetailPresenter(NewsDetailView view) {
        super(view);
    }

    public void getNews(final Long id){
        Subscription subscribes = Observable.create(new Observable.OnSubscribe<News>() {
            @Override
            public void call(Subscriber<? super News> subscriber) {
                News news = GreenDaoManager.getInstance().getNewSession().getNewsDao().queryBuilder()
                        .where(NewsDao.Properties.Id.eq(id)).unique();

                if(news!=null && news.getAudio_id() != null && news.getAudio_id() != 0L){
                    Audio audio = GreenDaoManager.getInstance().getNewSession().getAudioDao().queryBuilder()
                            .where(AudioDao.Properties.Id.eq(news.getAudio_id())).unique();
                    news.setAudio(audio);
                }

                if(news!=null && news.getVideo_id() != null && news.getVideo_id() !=0L){
                    Video video = GreenDaoManager.getInstance().getNewSession().getVideoDao().queryBuilder()
                            .where(VideoDao.Properties.Id.eq(news.getVideo_id())).unique();
                    news.setVideo(video);
                }
                subscriber.onNext(news);
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
                .subscribe(new Observer<News>() {
                    @Override
                    public void onNext(News data) {
                        if (mView != null) {
                            mView.getNewsSuccessed(data);
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
                            mView.getNewsSuccessed(null);
                            mView.hideProgress(Constant.PROGRESS_TYPE_DIALOG);
                        }

                    }
                });
    }
}
