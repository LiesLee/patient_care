package com.lieslee.patient_care.module.download_history.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.base.ui.BaseActivity;
import com.common.base.ui.BaseAdapter;
import com.common.utils.FastJsonUtil;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.FileDownLoadStatus;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.event.ME_NewsDelete;
import com.lieslee.patient_care.event.ME_NewsSave;
import com.lieslee.patient_care.event.ME_RedownloadNews;
import com.lieslee.patient_care.event.ME_StartDownLoad;
import com.lieslee.patient_care.module.download_history.ui.activity.NewsDetailActivity;
import com.lieslee.patient_care.utils.DialogHelper;
import com.lieslee.patient_care.utils.FileUtils;
import com.lieslee.patient_care.utils.GlideUtil;
import com.lieslee.patient_care.utils.UIHelper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.socks.library.KLog;
import com.views.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LiesLee on 17/6/21.
 */

public class NewsAdapter extends BaseAdapter<News> {

    public volatile Long downloading_id = -1l;
    public volatile Long redownloading_id = -1l;

    public NewsAdapter(Context ctx, int layoutResId, List<News> data) {
        super(ctx, layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final News data) {
        baseViewHolder.setText(R.id.tv_item_title, data.getTitle());
        baseViewHolder.getView(R.id.tv_content).setVisibility(TextUtils.isEmpty(data.getDescription()) ? View.INVISIBLE : View.VISIBLE);
        baseViewHolder.setText(R.id.tv_content, data.getDescription());

        if (data.getMedia_type() == 1) { //has video
            ImageView iv_item_img = baseViewHolder.getView(R.id.iv_item_img);
            if (TextUtils.isEmpty(data.getCover_image())) { //no cover
                iv_item_img.setImageResource(R.mipmap.bg_no_cover);
            } else {
                String url = data.getCover_image();
                GlideUtil.loadImage((Activity) mContext, url, iv_item_img);
            }
            baseViewHolder.setVisible(R.id.rl_media, true);
        } else {
            baseViewHolder.setVisible(R.id.rl_media, false);
        }

        RoundCornerProgressBar rvpb_bar = baseViewHolder.getView(R.id.rvpb_bar);
        rvpb_bar.setMax(data.getFileDownLoadStatus().size() * 100);


        if (data.getDownload_status() == 0) {

            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.line_color));
            baseViewHolder.setText(R.id.tv_download_tips, "等待中");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(0f);
            baseViewHolder.setText(R.id.tv_percent, "0.00%");

        } else if (data.getDownload_status() == 1) {
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_blue));
            baseViewHolder.setText(R.id.tv_download_tips, "下载中");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(data.getNowProgress());
            if(data.getNowProgress() == 0f){
                baseViewHolder.setText(R.id.tv_percent, "0.00%");
            }else{
                BigDecimal so = new BigDecimal(data.getNowProgress() * 100);
                BigDecimal total = new BigDecimal(data.getFileDownLoadStatus().size() *100);
                baseViewHolder.setText(R.id.tv_percent, so.divide(total, 2, BigDecimal.ROUND_HALF_UP).floatValue() + "%");
            }
        } else if (data.getDownload_status() == 2) {
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_green));
            baseViewHolder.setText(R.id.tv_download_tips, "已完成");
            baseViewHolder.setText(R.id.tv_percent, "100.00%");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.INVISIBLE);
        }else if (data.getDownload_status() == -1){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.red));
            baseViewHolder.setText(R.id.tv_download_tips, "下载失败");
            baseViewHolder.setText(R.id.tv_percent, "0.00%");
            rvpb_bar.setProgress(0f);
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
        }

        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.getDownload_status() == 2){ //
                    Intent intent = new Intent(mContext, NewsDetailActivity.class);
                    intent.putExtra("id", data.getId());
                    mContext.startActivity(intent);
                }else if(data.getDownload_status() == -1){
                    popExcuteDialog(data);
                }else{
                    DialogHelper.showTipsDialog((BaseActivity) mContext, "下载成功以后才能查看哦 ~", "好的", null);
                }
            }
        });

        baseViewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popExcuteDialog(data);
                return false;
            }
        });
    }

    /**
     *
     * @param task
     * @param state 0:completed one file、  1 error
     */
    public void upDateItem(BaseDownloadTask task, int state){
        Long id = (Long) task.getTag();
        News news = getNewsById(id);
        if (news != null) {
            if(state == 0){
                for (FileDownLoadStatus status : news.getFileDownLoadStatus()) {
                    if (task.getUrl().equals(status.getUrl())) {
                        status.setProgress(100f);
                        status.setDone(true);
                        break;
                    }

                }
                news.setProgress(news.getProgress() + 1f);
                KLog.e("===Progress==="+news.getProgress());
                if(news.getProgress() == news.getFileDownLoadStatus().size()){
                    if(UIHelper.newsFileisExit((BaseActivity) mContext, news)){
                        news.setDownload_status(2);
                        KLog.e("news下载完成");
                    }else{
                        news.setDownload_status(-1);
                        news.setProgress(0);
                        KLog.e("news下载失败");
                    }
                    //发送消息通知观察者
                    EventBus.getDefault().post(new ME_StartDownLoad(0));
                    EventBus.getDefault().post(new ME_NewsSave(news));
                }

            }else if (state == 1){
                news.setDownload_status(-1);
                news.setProgress(0);
                KLog.e("news下载失败");
                //发送消息通知观察者
                EventBus.getDefault().post(new ME_StartDownLoad(0));
                EventBus.getDefault().post(new ME_NewsSave(news));
            }

        }
        notifyDataSetChanged();
    }


    /**
     * 根据每个文件下载进度更新总进度
     * @param task
     * @param soFarBytes
     * @param totalBytes
     */
    public void updateProgress(BaseDownloadTask task, int soFarBytes, int totalBytes){
        float progress= 0f;
        if(soFarBytes != 0){
            Long id = (Long) task.getTag();
            BigDecimal so = new BigDecimal(soFarBytes * 100);
            BigDecimal total = new BigDecimal(totalBytes);
            progress= so.divide(total, 2, BigDecimal.ROUND_HALF_UP).floatValue();
            News news = getNewsById(id);
            for (FileDownLoadStatus status : news.getFileDownLoadStatus()) {
                    if (task.getUrl().equals(status.getUrl())) {
                        if(progress >= status.getProgress()){
                            status.setProgress(progress);
                        }
                        status.setDone(false);
                        break;
                    }

            }
        }
        notifyDataSetChanged();

    }

    /**
     * 重新下载完成状态
     * @param task
     * @param state 0:completed one file、  1 error
     */
    public void redownloadUpDateItem(BaseDownloadTask task, int state, Throwable e){
        Long id = (Long) task.getTag();
        News news = getNewsById(id);
        if (news != null) {
            if(state == 0){
                for (FileDownLoadStatus status : news.getFileDownLoadStatus()) {
                    if (task.getUrl().equals(status.getUrl())) {
                        status.setProgress(100f);
                        status.setDone(true);
                        break;
                    }

                }
                news.setProgress(news.getProgress() + 1f);
                KLog.e("===Progress==="+news.getProgress());
                if(news.getProgress() == news.getFileDownLoadStatus().size()){
                    news.setDownload_status(2);
                    KLog.e("news重新下载完成");
                    EventBus.getDefault().post(new ME_NewsSave(news));
                }

            }else if (state == 1){
                news.setDownload_status(-1);
                news.setProgress(0);
                KLog.e("news重新下载失败");

                String error = "";
                if(e != null && e instanceof FileDownloadHttpException){
                    error = ((FileDownloadHttpException)e).getCode() + error;
                }
                DialogHelper.showTipsDialog((BaseActivity) mContext, "重新下载失败\n \n《"+news.getTitle()
                        +"》  文件不存在或链接出错，错误码：\n"+error+"\n\n可联系相关客服解决！", "好的", null);

                //发送消息通知观察者
                EventBus.getDefault().post(new ME_NewsSave(news));
            }

        }
        notifyDataSetChanged();
    }



    public boolean findDownloadItem() {
        boolean hasDownload = false;
        Iterator<News> it = getData().iterator();
        while (it.hasNext()){
            News news = it.next();
            if(news.getDownload_status() == 0){
                downloading_id = news.getId();
                hasDownload = true;
            }
        }
        return hasDownload;
    }

    public News getNewsById(Long id){
        Iterator<News> it = getData().iterator();
        while (it.hasNext()){
            News news = it.next();
            if(news.getId().equals(id)){
                return news;
            }
        }
        return null;
    }



    public void popExcuteDialog(final News news){
        final int state = news.getDownload_status();
        if(state == 0 || state == 1) return;
        List<String> list;
        if(state == -1){
            list = Arrays.asList("重新下载", "删除");
        }else{
            list = Arrays.asList("删除");
        }
        DialogHelper.DialogOnclickSelectCallback callback = new DialogHelper.DialogOnclickSelectCallback() {
            @Override
            public void onButtonClick(Dialog dialog, int position) {
                if(state == -1){
                    switch (position) {
                        case 0 :
                            EventBus.getDefault().post(new ME_RedownloadNews(news));
                            break;
                        case 1 :
                            delete(news);
                            break;

                        default:
                            break;
                    }
                }else{
                    delete(news);
                }
            }
        };
        DialogHelper.showExcuteDialog((BaseActivity) mContext, list, callback);

    }

    private void delete(News news){
        EventBus.getDefault().post(new ME_NewsDelete(news));
        Iterator<News> it = getData().iterator();
        while (it.hasNext()){
            if(it.next().getId().equals(news.getId())){
                it.remove();
                break;
            }
        }
        ToastUtil.showShortToast(mContext, "删除成功");
        notifyDataSetChanged();
    }
}
