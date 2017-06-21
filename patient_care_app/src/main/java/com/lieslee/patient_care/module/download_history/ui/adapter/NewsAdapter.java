package com.lieslee.patient_care.module.download_history.ui.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.common.base.ui.BaseAdapter;

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

    }
}
