package com.lieslee.patient_care.module.common.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.presenter.BasePresenterImpl;
import com.common.base.ui.BaseFragment;
import com.common.base.ui.BaseView;
import com.lieslee.patient_care.R;
import com.views.ProgressWebView;
import com.views.ProgressWheel;
import com.views.util.ViewUtil;

/**
 * Created by LiesLee on 16/10/21.
 */
@ActivityFragmentInject(contentViewId = R.layout.act_webview)
public class WebViewFragment extends BaseFragment{
    private ProgressWebView webview;
    private ProgressWheel pw_loding;

    @Override
    protected void initView(View fragmentRootView) {
        webview = (ProgressWebView) findViewById(R.id.webview);
        pw_loding = (ProgressWheel) findViewById(R.id.pw_loding);

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
                    startActivity(intent);
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

        String url = getArguments().getString("url");
        webview.loadUrl(url);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
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

    @Override
    public void initData() {

    }
}
