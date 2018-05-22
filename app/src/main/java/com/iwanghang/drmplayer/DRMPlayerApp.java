package com.iwanghang.drmplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.iwanghang.drmplayer.utils.Constant;
import com.lidroid.xutils.DbUtils;

/**
 * Created by iwanghang on 16/4/26.
 */
public class DRMPlayerApp extends Application{

    //SharedPreferences是Android平台上一个轻量级的存储类，用来保存应用的一些常用配置
    public static SharedPreferences sp;//SharedPreferences 直译为 共享偏好
    //xutils 用于音乐收藏数据库 https://github.com/wyouflf/xUtils
    public static DbUtils dbUtils;
    //context 上下文 提供给AppUtils获取上下文
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        //实例化,存储名称为SP_NAME,存储模式为私有
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        //目的,比如在退出Activity时,保存循环模式,歌曲位置(第几首歌曲)
        //这里,我在MainActivity的onDestroy时,调用SharedPreferences,保存进度值

        //实例化,存储名称为DB_NAME
        dbUtils = DbUtils.create(getApplicationContext(),Constant.DB_NAME);

        //context 上下文 提供给AppUtils获取上下文
        context = getApplicationContext();
    }
}
