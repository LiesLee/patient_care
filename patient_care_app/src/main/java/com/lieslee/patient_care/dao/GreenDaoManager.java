package com.lieslee.patient_care.dao;

import com.lieslee.patient_care.application.PatientCareApplication;
import com.lieslee.patient_care.dao.gen.DaoMaster;
import com.lieslee.patient_care.dao.gen.DaoSession;

/**
 * Created by LiesLee on 2017/7/11.
 * Email: LiesLee@foxmail.com
 */

public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private MySQLiteOpenHelper devOpenHelper;


    public GreenDaoManager() {
        //创建一个数据库
        //devOpenHelper = new DaoMaster.DevOpenHelper(PatientCareApplication.getInstance(), "greendao-db", null);
        devOpenHelper = new MySQLiteOpenHelper(PatientCareApplication.getInstance(), "greendao-db", null);

        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

    /**
     * 关闭数据连接
     */
    public void close() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
        }
    }
}
