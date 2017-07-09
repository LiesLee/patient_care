package com.lieslee.patient_care.module.download_history.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.base.ui.BaseAdapter;
import com.common.utils.FastJsonUtil;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.FileDownLoadStatus;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.event.ME_StartDownLoad;
import com.lieslee.patient_care.module.download_history.ui.activity.NewsDetailActivity;
import com.lieslee.patient_care.utils.GlideUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by LiesLee on 17/6/21.
 */

public class NewsAdapter extends BaseAdapter<News> {

    public volatile int downPosition = -1;
    public volatile Long downloading_id = -1l;

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
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra("id", data.getId());
                mContext.startActivity(intent);
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
                news.setProgress(news.getProgress() + 1f);
                KLog.e("===Progress==="+news.getProgress());
                if(news.getProgress() == news.getFileDownLoadStatus().size()){
                    news.setDownload_status(2);
                    KLog.e("news下载完成");
                    KLog.json(FastJsonUtil.t2Json2(news));
                    //发送消息通知观察者
                    EventBus.getDefault().post(new ME_StartDownLoad(0));
                }

                for (FileDownLoadStatus status : news.getFileDownLoadStatus()) {
                    if (task.getUrl().equals(status.getUrl())) {
                        status.setProgress(100f);
                        status.setDone(true);
                        break;
                    }

                }

            }else if (state == 1){
                news.setDownload_status(-1);
                news.setProgress(0);
                KLog.e("news下载失败");
                KLog.json(FastJsonUtil.t2Json2(news));
                //发送消息通知观察者
                EventBus.getDefault().post(new ME_StartDownLoad(0));
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
            BigDecimal so = new BigDecimal(soFarBytes * 100);
            BigDecimal total = new BigDecimal(totalBytes);
            progress= so.divide(total, 2, BigDecimal.ROUND_HALF_UP).floatValue();
            News news = getNewsById(downloading_id);
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

    public boolean findDownloadItem() {
        boolean hasDownload = false;
        for (int i = 0; i < mData.size(); i++) {
            News news = mData.get(i);
            if (news.getDownload_status() == 0) {
                downPosition = i;
                downloading_id = news.getId();
                hasDownload = true;
                break;
            }
        }
        return hasDownload;
    }

    public News getNewsById(Long id){
        for (News n : getData()){
            if(n.getId().equals(id)){
                return  n;
            }
        }
        return null;
    }
}
