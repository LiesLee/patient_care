package com.lieslee.patient_care.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.base.ui.BaseActivity;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.bean.News;
import com.lieslee.patient_care.module.common.ui.fragment.WebViewFragment;
import com.views.ProgressWebView;
import com.views.ProgressWheel;
import com.views.util.ToastUtil;
import com.views.util.ViewUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LiesLee on 16/9/29.
 */
public class UIHelper {

    public static final int INTENT_REQUEST_GET_IMAGES = 13;

    public static void showShakeAnim(Context context, View view, String toast) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake_x);
        view.startAnimation(shake);
        view.requestFocus();
        if(!TextUtils.isEmpty(toast)){
            ToastUtil.showShortToast(context, toast);
        }

    }

    public static boolean phoneNumberValid(String number) {
        // 手机号固定在5-20范围内
        if (number.length() < 5 || number.length() > 20) {
            return false;
        }

        String match = "";
        if (number.length() != 11) {
            return false;
        } else {
            // match = "^[1]{1}[0-9]{2}[0-9]{8}$";
            match = "^(1[3456789])\\d{9}$";
        }

        // 正则匹配
        if (!"".equals(match)) {
            return number.matches(match);
        }
        return true;
    }

    public static void initWebView(final BaseActivity baseActivity, final ProgressWebView webview, final ProgressWheel pw_loding, String url){

        class WebChromeClient extends android.webkit.WebChromeClient {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    webview.progressbar.setVisibility(View.GONE);

                    if(pw_loding.getVisibility() == View.VISIBLE) {
                        pw_loding.startAnimation(AnimationUtils.loadAnimation(baseActivity, R.anim.activity_close));
                        pw_loding.setVisibility(View.GONE);
                    }
                } else {
                    if (pw_loding.getVisibility() == View.GONE){
                        pw_loding.startAnimation(AnimationUtils.loadAnimation(baseActivity, R.anim.activity_open));
                        pw_loding.setVisibility(View.VISIBLE);
                    }
                    if (webview.progressbar.getVisibility() == View.GONE)
                        webview.progressbar.setVisibility(View.VISIBLE);
                    webview.progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        }

        //圈圈进度条
        pw_loding.setBarWidth(ViewUtil.dp2px(baseActivity, 5));
        pw_loding.setBarColor(Color.parseColor("#1f97f1"));
        pw_loding.spin();
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setHorizontalScrollbarOverlay(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.requestFocus();
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebChromeClient(new WebChromeClient());
        if (webview.getSettings() != null) {
            webview.getSettings().setJavaScriptEnabled(true);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) return true;

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    baseActivity.startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }


        });

        webview.loadUrl(url);

    }

    public static long getLastTimeOnList(List<News> list){
        long time = 0;
        for(News news : list){
            if(news.getTimestamp() > time) time = news.getTimestamp();
        }
        return time;
    }

    public static void sortNews(List<News> list){
        Collections.sort(list, new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {  //Descending order
                return o1.getTimestamp() < o2.getTimestamp() ? 1 : -1;
            }
        });
    }

}
