package com.lieslee.patient_care.module.common.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import com.common.annotation.ActivityFragmentInject;
import com.common.base.ui.BaseActivity;
import com.common.utils.NetUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lieslee.patient_care.BuildConfig;
import com.lieslee.patient_care.R;
import com.lieslee.patient_care.application.PatientCareApplication;
import com.lieslee.patient_care.dao.GreenDaoManager;
import com.lieslee.patient_care.utils.DialogHelper;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LiesLee on 2017/6/24.
 * Email: LiesLee@foxmail.com
 */
@ActivityFragmentInject(contentViewId = R.layout.act_launcher)
public class LauncherActivity extends BaseActivity implements MultiplePermissionsListener {
    List<String> pList = new ArrayList<>();
    private long time;
    private Timer timer;
    private long c_time;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 隐藏状态栏
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {

        pList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        pList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        pList.add(Manifest.permission.READ_PHONE_STATE);

        Dexter.initialize(PatientCareApplication.getInstance()); //权限封装类
        //  获取权限
        Dexter.checkPermissions(LauncherActivity.this, pList);
        GreenDaoManager.getInstance();
        //初始化SDK
        KLog.init(BuildConfig.DEBUG);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.getDeniedPermissionResponses() == null || report.getDeniedPermissionResponses().isEmpty()) {
            //toast("正在初始化，请稍后 ~ ");
            iniTimer();

        } else {//denied permission responses isn't empty
            DialogHelper.show2btnDialog(baseActivity, "缺少部分权限将无法正常使用，请打开您系统应用设置页面点击'"+getResources().getString(R.string.app_name)+"'，然后开启相关权限！",  "取消", "好的",
                    false, new DialogHelper.DialogOnclickCallback() {
                        @Override
                        public void onButtonClick(Dialog dialog) {
                            //  获取权限
                            Dexter.checkPermissions(LauncherActivity.this, pList);
                        }
                    }, new DialogHelper.DialogOnclickCallback() {
                        @Override
                        public void onButtonClick(Dialog dialog) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", baseActivity.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            finish();
                        }
                    }).setCancelable(false);
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        token.continuePermissionRequest();
    }

    //倒计时，控制页面跳转
    private void iniTimer() {
        time = System.currentTimeMillis();
        timer = new Timer();// 实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                c_time = System.currentTimeMillis();
                if(c_time - time > 1500){ //大于*秒直接跳过
                    gotoMainActivity();
                }
            }
        }, 0, 1000);// 这里百毫秒
    }

    private void gotoMainActivity() {
        timer.cancel();
        Intent faceIntent = new Intent(baseActivity, MainActivity.class);
        startActivity(faceIntent);
        finish();
    }
}
