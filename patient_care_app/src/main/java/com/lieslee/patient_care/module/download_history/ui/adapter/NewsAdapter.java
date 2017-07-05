package com.lieslee.patient_care.module.download_history.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.arialyy.aria.core.download.DownloadEntity;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.base.ui.BaseAdapter;
import com.common.utils.FastJsonUtil;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.FileDownLoadStatus;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.event.ME_StartDownLoad;
import com.lieslee.patient_care.module.download_history.ui.activity.NewsDetailActivity;
import com.lieslee.patient_care.utils.GlideUtil;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by LiesLee on 17/6/21.
 */

public class NewsAdapter extends BaseAdapter<News> {

    private volatile int downPosition = -1;

    public NewsAdapter(Context ctx, int layoutResId, List<News> data) {
        super(ctx, layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final News data) {

        int position = getFinalPositionOnList(baseViewHolder);

        baseViewHolder.setText(R.id.tv_item_title, data.getTitle());

        baseViewHolder.getView(R.id.tv_content).setVisibility(TextUtils.isEmpty(data.getDescription()) ? View.INVISIBLE : View.VISIBLE);
        baseViewHolder.setText(R.id.tv_content, data.getDescription());

        if(data.getMedia_type() == 1){ //has video
            ImageView iv_item_img = baseViewHolder.getView(R.id.iv_item_img);
            if(TextUtils.isEmpty(data.getCover_image())){ //no cover
                iv_item_img.setImageResource(R.mipmap.bg_default);
            }else{
                String url = data.getCover_image();
                GlideUtil.loadImage((Activity)mContext, url, iv_item_img);
            }
            baseViewHolder.setVisible(R.id.rl_media, true);
        }else{
            baseViewHolder.setVisible(R.id.rl_media, false);
        }

        RoundCornerProgressBar rvpb_bar = baseViewHolder.getView(R.id.rvpb_bar);
        rvpb_bar.setMax(data.getFileDownLoadStatus().size() * 100);


        if(data.getDownload_status() == 0){

            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.line_color));
            baseViewHolder.setText(R.id.tv_download_tips, "未下载");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(5f);
            baseViewHolder.setText(R.id.tv_percent, "0.00%");

        }else if(data.getDownload_status() == 1){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_blue));
            baseViewHolder.setText(R.id.tv_download_tips, "下载中");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(data.getNowProgress());
            baseViewHolder.setText(R.id.tv_percent, data.getNowProgress() * 100 / (data.getFileDownLoadStatus().size() * 100) +"%");
        }else if(data.getDownload_status() == 2){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_green));
            baseViewHolder.setText(R.id.tv_download_tips, "已完成");
            baseViewHolder.setText(R.id.tv_percent, "100.00%");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.INVISIBLE);
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


    public void updateState(DownloadEntity downloadEntity){
        if(downloadEntity.getState() == DownloadEntity.STATE_RUNNING){ //正在下载...
            News news = getData().get(downPosition);
            if(news!=null){
                news.setDownload_status(1);
                for(FileDownLoadStatus status : news.getFileDownLoadStatus()){
                    if(downloadEntity.getDownloadUrl().equals(status.getUrl())){
                        float progress =  downloadEntity.getFileSize() == 0 || downloadEntity.getCurrentProgress() == 0 ?
                                0 : (downloadEntity.getCurrentProgress() * 100 / downloadEntity.getFileSize());
                        status.setProgress(progress);
                        status.setDone(false);
                        break;
                    }

                }


            }


        }else if(downloadEntity.getState() == DownloadEntity.STATE_COMPLETE){ //完成
            News news = getData().get(downPosition);
            if(news!=null){
                for(FileDownLoadStatus status : news.getFileDownLoadStatus()){
                    if(downloadEntity.getDownloadUrl().equals(status.getUrl())){
                        KLog.json(FastJsonUtil.t2Json2(status));
                        status.setProgress(100);
                        status.setDone(true);
                        break;
                    }

                }
                boolean isAllDone = true;
                for(FileDownLoadStatus status : news.getFileDownLoadStatus()){
                    if(!status.isDone()){
                        isAllDone = false;
                        break;
                    }
                }
                if(isAllDone){//下载完成
                    news.setDownload_status(2);
                    news.setProgress(news.getFileDownLoadStatus().size() * 100);
                    KLog.e("news下载完成");
                    KLog.json(FastJsonUtil.t2Json2(news));
                    //发送消息通知观察者
                    EventBus.getDefault().post(new ME_StartDownLoad(0));
                }
            }
        }

        notifyDataSetChanged();
    }

    public int getDownPosition() {
        return downPosition;
    }

    public void setDownPosition(int downPosition) {
        this.downPosition = downPosition;
    }

    public boolean findDownloadItem(){
        boolean hasDownload = false;
        for(int i = 0;i<mData.size();i++){
            News news = mData.get(i);
            if(news.getDownload_status() != 2 ){
                downPosition = i;
                hasDownload = true;
                break;
            }
        }
        return hasDownload;
    }
}
