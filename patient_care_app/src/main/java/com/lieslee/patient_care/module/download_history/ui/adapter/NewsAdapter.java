package com.lieslee.patient_care.module.download_history.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.base.ui.BaseAdapter;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.module.news.ui.activity.NewsDetailActivity;

import java.util.List;

/**
 * Created by LiesLee on 17/6/21.
 */

public class NewsAdapter extends BaseAdapter<String> {

    public NewsAdapter(Context ctx, int layoutResId, List<String> data) {
        super(ctx, layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String s) {
        RoundCornerProgressBar rvpb_bar = baseViewHolder.getView(R.id.rvpb_bar);
        int position = getFinalPositionOnList(baseViewHolder);
        if(position == 0){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_green));
            baseViewHolder.setText(R.id.tv_download_tips, "已下载");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.INVISIBLE);
        }else if(position == 1){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_blue));
            baseViewHolder.setText(R.id.tv_download_tips, "下载中");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(50f);
        }else{
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.line_color));
            baseViewHolder.setText(R.id.tv_download_tips, "未下载");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(2f);
        }

        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, NewsDetailActivity.class));
            }
        });
    }
}
