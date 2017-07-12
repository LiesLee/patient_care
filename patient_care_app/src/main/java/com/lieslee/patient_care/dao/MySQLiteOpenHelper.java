package com.lieslee.patient_care.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.lieslee.patient_care.dao.gen.AudioDao;
import com.lieslee.patient_care.dao.gen.DaoMaster;
import com.lieslee.patient_care.dao.gen.NewsDao;
import com.lieslee.patient_care.dao.gen.VideoDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by LiesLee on 2017/7/11.
 * Email: LiesLee@foxmail.com
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, NewsDao.class, VideoDao.class, AudioDao.class);
    }
}
