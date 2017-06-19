package com.lieslee.patient_care.application;

import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.karumi.dexter.Dexter;
import com.socks.library.KLog;
import com.lieslee.patient_care.BuildConfig;

/**
 * Created by LiesLee on 2017/6/19.
 * Email: LiesLee@foxmail.com
 */

public class PatientCareApplication extends MultiDexApplication {
    public static volatile PatientCareApplication instance;
    private Handler handler;
    //private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler();
        KLog.init(BuildConfig.DEBUG);
        Dexter.initialize(this); //权限封装类
    }

    public static PatientCareApplication getInstance() {
        return instance;
    }


    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
