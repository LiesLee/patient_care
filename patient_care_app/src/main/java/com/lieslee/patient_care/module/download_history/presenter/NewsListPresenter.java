package com.lieslee.patient_care.module.download_history.presenter;

import com.common.base.presenter.BasePresenterImpl;
import com.common.callback.RequestCallback;
import com.lieslee.patient_care.bean.Audio;
import com.lieslee.patient_care.bean.NewsListResponse;
import com.lieslee.patient_care.common.Constant;
import com.lieslee.patient_care.dao.GreenDaoManager;
import com.lieslee.patient_care.dao.gen.NewsDao;
import com.lieslee.patient_care.http.HttpUtil;
import com.lieslee.patient_care.http.protocol.CommonProtocol;
import com.lieslee.patient_care.module.download_history.view.NewsListView;
import com.lieslee.patient_care.utils.UIHelper;

import java.util.List;

/**
 * Created by LiesLee on 17/6/30.
 */

public class NewsListPresenter extends BasePresenterImpl<NewsListView> {
    public NewsListPresenter(NewsListView view) {
        super(view);
    }

    /**
     * when first load id=0, than the last
     * @param update_time
     */
    public void loadNewsList(long update_time){
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
                if(mView == null) return;
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
}
