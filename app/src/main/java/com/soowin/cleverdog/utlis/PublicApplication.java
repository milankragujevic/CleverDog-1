package com.soowin.cleverdog.utlis;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.soowin.cleverdog.adapter.index.VoiceAdapter;
import com.soowin.cleverdog.dataBase.DBMethond;
import com.soowin.cleverdog.http.HttpUrl;

import java.io.File;

/**
 * Created by Administrator on 2017/6/8.
 */

public class PublicApplication extends Application {
    private static Context CONTEXT = null;
    /**
     * 获取数据路径拼接接口
     **/
    public static HttpUrl urlData = new HttpUrl();
    /**
     * 本地数据库 存储数据
     */
    public static SharedPreferences loginInfo;
    /**
     * 数据库路径
     **/
    public static String databasesPath;
    /**
     * 头像路径
     **/
    public static String pathAvatar = "";
    /**
     * 数据库
     **/
    public static DBMethond db;
    /**
     * 语音队列管理
     *
     * @return
     */
    public static VoiceManage mVoiceManage;
    /**
     * 禁停播报时间间隔
     *
     * @return
     */
    public static int speakSleep = 5;

    /**
     * 语音队列adapter
     *
     * @return
     */
    public static VoiceAdapter mAdapter;
    /**
     * 变量 速度，延迟
     */
    public static double mVariableSpeed = 5;
    public static double mVariableTime = 10;
    public static double mapTime = 2;
    public static double longVariable = 10;
    /**
     * 播报的长度
     *
     * @return
     */
    public static double longOne = 20 + longVariable + mVariableSpeed * mVariableTime * mapTime;
    public static double longTwo = 100 + longVariable + mVariableSpeed * mVariableTime * mapTime;

    /**
     * 本地设置缓存
     *
     * @return
     */
    public static SharedPreferences settingInfo;

    /**
     * 是否为微信登录回调
     *
     * @return
     */
    public static boolean isLoginCallback = true;

    /**
     * 微信分享内容
     *
     * @return
     */
    public static String WXShare = "灵狗驾驶预警，让你轻松出行，远离罚单!";

    /**
     * 微信登录注册
     *
     * @return
     */
    public static int WXLogin = 0;
    public static String WXopenid = "";
    public static String WXunionid = "";

    /**
     * activity 栈 管理类
     *
     * @return
     */
    public static ScreenManager publicScreenManager;

    public static Context getContext() {
        return CONTEXT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loginInfo = getSharedPreferences("ClDo", 0);
        settingInfo = getSharedPreferences("Setting", 0);
        CONTEXT = this.getApplicationContext();
        CookieSyncManager.createInstance(this);
        File externalDir = Environment.getExternalStorageDirectory();
        //头像地址
        File path = new File(externalDir.getAbsolutePath() + "/CleverDog/Images");
        pathAvatar = path + "/heard.jpg";
        //数据库地址
        databasesPath = externalDir.getAbsolutePath() + "/CleverDog/DataBases";
        if (!path.exists()) { // 判断目录是否存在
            if (path.mkdirs()) {
                Log.e("dbMethond", "mkdirs-->true");
            }// 创建目录
        }
        publicScreenManager = ScreenManager.getScreenManager();
        mAdapter = new VoiceAdapter();
        initDB();
        initXunFei();
    }

    /**
     * 数据库初始化
     */
    private void initDB() {
        db = new DBMethond();
    }

    //初始化讯飞语音
    private void initXunFei() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=599a2cc5");
//        SpeechUtility.createUtility(this, APPID + "=599a2cc5," + SpeechConstant.FORCE_LOGIN + "=true");
        mVoiceManage = new VoiceManage(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //内存低的时候,调用垃圾回收器
        System.gc();
    }
}
