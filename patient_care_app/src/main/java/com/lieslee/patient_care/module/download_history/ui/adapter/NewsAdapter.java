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
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.module.download_history.ui.activity.NewsDetailActivity;
import com.lieslee.patient_care.utils.GlideUtil;

import java.util.List;

/**
 * Created by LiesLee on 17/6/21.
 */

public class NewsAdapter extends BaseAdapter<News> {

    public NewsAdapter(Context ctx, int layoutResId, List<News> data) {
        super(ctx, layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final News data) {

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
        int position = getFinalPositionOnList(baseViewHolder);
        if(position == 0){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_green));
            baseViewHolder.setText(R.id.tv_download_tips, "已下载");
            baseViewHolder.setText(R.id.tv_percent, "100.00%");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.INVISIBLE);
        }else if(position == 1){
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.pink_blue));
            baseViewHolder.setText(R.id.tv_download_tips, "下载中");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(50f);
            baseViewHolder.setText(R.id.tv_percent, "50.00%");
        }else{
            baseViewHolder.setBackgroundColor(R.id.tv_download_tips, mContext.getResources().getColor(R.color.line_color));
            baseViewHolder.setText(R.id.tv_download_tips, "未下载");
            baseViewHolder.getView(R.id.ll_progress_layout).setVisibility(View.VISIBLE);
            rvpb_bar.setProgress(5f);
            baseViewHolder.setText(R.id.tv_percent, "0.00%");
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
}
