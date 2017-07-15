package com.lieslee.patient_care.module.common.ui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.utils.wight.LandLayoutVideo;
import com.lieslee.patient_care.utils.wight.SampleListener;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import butterknife.Bind;

/**
 * Created by LiesLee on 2017/7/15.
 * Email: LiesLee@foxmail.com
 */

@ActivityFragmentInject(contentViewId = R.layout.act_video)
public class VideoActivity extends BaseActivity {
    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @Bind(R.id.detail_player)
    LandLayoutVideo detailPlayer;
    private String videoPath;
    private String title;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        videoPath =  getIntent().getStringExtra("url");
        title =  getIntent().getStringExtra("title");
        detailPlayer.setUp(videoPath, false, null, title);

        resolveNormalVideoUI();

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        orientationUtils.setRotateWithSystem(true);

        detailPlayer.setIsTouchWiget(true);
        //detailPlayer.setIsTouchWigetFull(false);
        //关闭自动旋转
        detailPlayer.setRotateViewAuto(false);
        detailPlayer.setLockLand(false);
        detailPlayer.setShowFullAnimation(false);
        detailPlayer.setNeedLockFull(true);
        detailPlayer.setSeekRatio(1);
        //detailPlayer.setOpenPreView(false);

        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(baseActivity, true, true);
            }
        });

        detailPlayer.setStandardVideoAllCallBack(new SampleListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
            }
        });
        detailPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });
        isPlay= true;
        detailPlayer.startPlayLogic();

        detailPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void initData() {



    }


    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        GSYVideoPlayer.releaseAllVideos();
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        super.onDestroy();

    }

    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        detailPlayer.getTitleTextView().setText(title);
        detailPlayer.getBackButton().setVisibility(View.VISIBLE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!detailPlayer.isIfCurrentIsFullscreen()) {
                    detailPlayer.startWindowFullscreen(baseActivity, true, true);
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (detailPlayer.isIfCurrentIsFullscreen()) {
                    StandardGSYVideoPlayer.backFromWindowFull(this);
                }
                if (orientationUtils != null) {
                    orientationUtils.setEnable(true);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {

    }
}
