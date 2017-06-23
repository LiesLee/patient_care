package com.lieslee.patient_care.module.news.ui.activity;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.common.utils.DateUtil;
import com.common.utils.FileUtils;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.utils.DialogHelper;
import com.lieslee.patient_care.utils.UIHelper;
import com.socks.library.KLog;
import com.views.ProgressWebView;
import com.views.ProgressWheel;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

/**
 * Created by LiesLee on 2017/6/21.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.act_news_detail, toolbarTitle = R.string.news_detail)
public class NewsDetailActivity extends BaseActivity {
    @Bind(R.id.webview)
    ProgressWebView webview;
    @Bind(R.id.pw_loding)
    ProgressWheel pw_loding;

    @Bind(R.id.ll_audio)
    LinearLayout ll_audio;

    @Bind(R.id.rl_video)
    RelativeLayout rl_video;

    @Bind(R.id.tv_audio_title)
    TextView tv_audio_title;
    @Bind(R.id.tv_audio_subtitle)
    TextView tv_audio_subtitle;
    @Bind(R.id.tv_audio_progress_time)
    TextView tv_audio_progress_time;
    @Bind(R.id.tv_audio_total_time)
    TextView tv_audio_total_time;

    @Bind(R.id.iv_video_img)
    ImageView iv_video_img;
    @Bind(R.id.iv_audio_icon)
    ImageView iv_audio_icon;

    @Bind(R.id.rvpb_bar)
    RoundCornerProgressBar rvpb_bar;

    MediaPlayer mPlayer = new MediaPlayer();

    Handler mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private boolean isAudioPause = false;


    @Override
    protected void initView() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case 1 :
                        tv_audio_progress_time.setText("00:00");
                        tv_audio_total_time.setText(DateUtil.formatTime("mm:ss", mPlayer.getDuration()));
                        rvpb_bar.setProgress(0f);
                        rvpb_bar.setMax(mPlayer.getDuration());
                        isAudioPause = false;

                        break;
                    case 2 :
                        tv_audio_progress_time.setText(DateUtil.formatTime("mm:ss", mPlayer.getCurrentPosition()));
                        rvpb_bar.setProgress(mPlayer.getCurrentPosition());
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        UIHelper.initWebView(baseActivity, webview, pw_loding, "http://www.baidu.com");
        ll_audio.setOnClickListener(this);

        iniMediaPlayerListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_audio :
                playpause();
                break;

            default:
                break;
        }
    }


    void playpause(){
        String audioPath = FileUtils.SDPATH + "/DCIM/mp3_test/test_audio.mp3";
        if(mPlayer == null) new MediaPlayer();

        if(mPlayer.isPlaying()){//正在播放
            mPlayer.pause();
            isAudioPause = true;
        }else{ //不在播放（可能是暂停）
            if(isAudioPause){
                isAudioPause = false;
                mPlayer.start();
            }else{
                try {
                    FileInputStream in = new FileInputStream(audioPath);
                    FileDescriptor fd = in.getFD();
                    mPlayer.reset();
                    mPlayer.setDataSource(fd);
                    mPlayer.prepare();
                    KLog.e(mPlayer.getDuration());
                } catch (IOException e) {
                    DialogHelper.showTipsDialog(baseActivity, "播放出问题了，播放的文件不正确或文件被而已删除了😭", "确定", null);
                    KLog.e(e);
                }
            }

        }
        iv_audio_icon.setImageResource(mPlayer.isPlaying() ? R.mipmap.icon_stop : R.mipmap.icon_sound);
    }

    void iniMediaPlayerListener(){
        //播放完成
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(1);
                iv_audio_icon.setImageResource(R.mipmap.icon_sound);
                if(mTimer!=null) mTimer.cancel();
            }
        });
        //准备播放
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mHandler.sendEmptyMessage(1);
                mPlayer.start();
                iv_audio_icon.setImageResource(R.mipmap.icon_stop);
                //每隔N毫秒检测一下播放进度
                //----------定时器记录播放进度---------//
                mTimer =new Timer();
                mTimerTask =new TimerTask() {
                    @Override
                    public void run() {
                        if(mPlayer.isPlaying()){
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                };
                mTimer.schedule(mTimerTask,0,500);
            }
        });
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                KLog.e("onBufferingUpdate", percent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mTimer!=null){
            mTimer.cancel();
        }

        if(mPlayer!=null){
            mPlayer.pause();
            isAudioPause = true;
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
    }
}
