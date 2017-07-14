package com.lieslee.patient_care.module.common.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.utils.FileUtils;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by LiesLee on 2017/6/25.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.act_play_video)
public class PlayVideoActivity extends BaseActivity {

    private String videoPath;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        videoPath =  getIntent().getStringExtra("url");
        title =  getIntent().getStringExtra("title");
        JCVideoPlayerStandard.startFullscreen(this, JCVideoPlayerStandard.class, videoPath, title);

    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            finish();
            return;
        }
        finish();
        super.onBackPressed();
    }



    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        finish();
    }

}
