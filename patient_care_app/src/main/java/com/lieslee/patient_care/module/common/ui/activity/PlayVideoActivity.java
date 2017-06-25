package com.lieslee.patient_care.module.common.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.common.utils.FileUtils;
import com.lieslee.patient_care.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by LiesLee on 2017/6/25.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.act_play_video)
public class PlayVideoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        String videoPath =  FileUtils.SDPATH + "/Download/test_video.mp4";
        JCVideoPlayerStandard.startFullscreen(this, JCVideoPlayerStandard.class, videoPath, "测试视频啊收到了饭卡了空手道解放立刻就受到法律；搭街坊；阿历克斯大家发；顺利打开附件a；撒开绿灯解放；阿拉山口大家发送");

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
