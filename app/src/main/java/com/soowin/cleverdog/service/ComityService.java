package com.soowin.cleverdog.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.info.service.CoordinateBean;
import com.soowin.cleverdog.info.service.NoStoppingBean;
import com.soowin.cleverdog.receiver.ScreenBroadcastListener;
import com.soowin.cleverdog.utlis.MapUtlis;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.StrUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hxt on 2017/8/30.
 * 车让人标示监听
 */

public class ComityService extends Service {
    public static final String TAG = "com.soowin.cleverdog.service.ComityService";

    //    private String mLatA = "0";
//    private String mLonA = "0";
//    private String mLatB = "0";
//    private String mLonB = "0";
    private boolean isFirst = true;//是否为第一次播放
    private double ydJiao = 0;//历史运动角
    Lock comityLock = new ReentrantLock();//线程锁
    double p_jiao_20 = 0;
    double p_jiao_100 = 0;
    //车让人
    private List<CoordinateBean> lDAComity100 = new ArrayList<>();
    private List<CoordinateBean> lDBComity100 = new ArrayList<>();
    private List<CoordinateBean> lDNComity100 = new ArrayList<>();
    private List<CoordinateBean> lDAComity20 = new ArrayList<>();
    private List<CoordinateBean> lDBComity20 = new ArrayList<>();
    private List<CoordinateBean> lDNComity20 = new ArrayList<>();
    //压实线违章
    private List<CoordinateBean> lDAYSX100 = new ArrayList<>();
    private List<CoordinateBean> lDBYSX100 = new ArrayList<>();
    private List<CoordinateBean> lDNYSX100 = new ArrayList<>();
    private List<CoordinateBean> lDAYSX20 = new ArrayList<>();
    private List<CoordinateBean> lDBYSX20 = new ArrayList<>();
    private List<CoordinateBean> lDNYSX20 = new ArrayList<>();
    //黄色网格违章
    private List<CoordinateBean> lDAHWG100 = new ArrayList<>();
    private List<CoordinateBean> lDBHWG100 = new ArrayList<>();
    private List<CoordinateBean> lDNHWG100 = new ArrayList<>();
    private List<CoordinateBean> lDAHWG20 = new ArrayList<>();
    private List<CoordinateBean> lDBHWG20 = new ArrayList<>();
    private List<CoordinateBean> lDNHWG20 = new ArrayList<>();
    //直行掉头违章
    private List<CoordinateBean> lDADT100 = new ArrayList<>();
    private List<CoordinateBean> lDBDT100 = new ArrayList<>();
    private List<CoordinateBean> lDNDT100 = new ArrayList<>();
    private List<CoordinateBean> lDADT20 = new ArrayList<>();
    private List<CoordinateBean> lDBDT20 = new ArrayList<>();
    private List<CoordinateBean> lDNDT20 = new ArrayList<>();
    //鸣笛违章
    private List<CoordinateBean> lDAMD100 = new ArrayList<>();
    private List<CoordinateBean> lDBMD100 = new ArrayList<>();
    private List<CoordinateBean> lDNMD100 = new ArrayList<>();
    private List<CoordinateBean> lDAMD20 = new ArrayList<>();
    private List<CoordinateBean> lDBMD20 = new ArrayList<>();
    private List<CoordinateBean> lDNMD20 = new ArrayList<>();
    //远光灯违章
    private List<CoordinateBean> lDAYGD100 = new ArrayList<>();
    private List<CoordinateBean> lDBYGD100 = new ArrayList<>();
    private List<CoordinateBean> lDNYGD100 = new ArrayList<>();
    private List<CoordinateBean> lDAYGD20 = new ArrayList<>();
    private List<CoordinateBean> lDBYGD20 = new ArrayList<>();
    private List<CoordinateBean> lDNYGD20 = new ArrayList<>();
    //特殊类型
    private List<CoordinateBean> lDATS100 = new ArrayList<>();
    private List<CoordinateBean> lDBTS100 = new ArrayList<>();
    private List<CoordinateBean> lDNTS100 = new ArrayList<>();
    private List<CoordinateBean> lDATS20 = new ArrayList<>();
    private List<CoordinateBean> lDBTS20 = new ArrayList<>();
    private List<CoordinateBean> lDNTS20 = new ArrayList<>();
    //禁止停车
    private List<NoStoppingBean> lDNNoStop = new ArrayList<>();
    private Polygon polygon;
    int noStopSleepTime = PublicApplication.speakSleep;

    private MyBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        public void startComity(final String lat, final String lon, boolean misFirst, double yd_jiao) {
            isFirst = misFirst;
            ydJiao = yd_jiao;
            p_jiao_20 = Double.parseDouble(getResources().getString(R.string.direction_20));//20角度偏移量
            p_jiao_100 = Double.parseDouble(getResources().getString(R.string.direction_100));//100角度偏移量
            new Thread() {
                @Override
                public void run() {
                    comityLock.lock();
                    try {
                        if (isFirst) {
                            PublicApplication.mVoiceManage.addVoice20("灵狗为您服务！");
                        }
                        jisuanTS(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("crr", false))
                            jisuanComity(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("ysx", false))
                            jisuanYSX(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("hwg", false))
                            jisuanHWG(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("dt", false))
                            jisuanDT(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("md", false))
                            jisuanMD(lat, lon);
                        if (PublicApplication.settingInfo.getBoolean("ygd", false))
                            jisuanYGD(lat, lon);
                    } finally {
                        comityLock.unlock();
                    }
                }
            }.start();
        }

        public void startNoStop(final String lat, final String lon, final AMap aMap) {
            new Thread() {
                @Override
                public void run() {
                    if (noStopSleepTime == PublicApplication.speakSleep) {
                        if (PublicApplication.settingInfo.getBoolean("jt", false))
                            jisuanNoStop(lat, lon, aMap);
                        noStopSleepTime = 0;
                    }
                    noStopSleepTime++;
                }
            }.start();
        }
    }

    /**
     * 车让人违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanComity(String lat, String lon) {
        lDBComity100.clear();
        lDNComity100.clear();
        lDBComity20.clear();
        lDNComity20.clear();
        lDBComity100 = MapUtlis.getCoordinate100(lat, lon, 1);
        lDBComity20.clear();
        lDBComity20.addAll(lDBComity100);
        if (lDBComity100 != null) {
            if (isFirst) {
                lDBComity100 = MapUtlis.getB(lat, lon, lDBComity100, PublicApplication.longTwo - 1);
                lDBComity100 = MapUtlis.jisuanFangXiang(lDBComity100, lat, lon);
                if (lDBComity100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBComity100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《车让人违章》抓拍");
                }//计算完毕放入历史数据
                lDAComity100.clear();
                lDAComity100.addAll(lDBComity100);
                lDAComity20.clear();
                lDAComity20.addAll(lDBComity100);
                return;
            }
            //周围坐标
            lDBComity20 = MapUtlis.getB(lat, lon, lDBComity20, PublicApplication.longOne);
            lDBComity100 = MapUtlis.getB(lat, lon, lDBComity100, PublicApplication.longTwo);

            //计算方向
            lDBComity20 = MapUtlis.jisuanFangXiang(lDBComity20, lat, lon);
            lDBComity100 = MapUtlis.jisuanFangXiang(lDBComity100, lat, lon);

            //移动方向内的点
            lDBComity20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBComity20);
            lDBComity100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBComity100);

            //新坐标
            lDNComity20 = MapUtlis.getNew(lDNComity20, lDBComity20, lDAComity20);
            lDNComity100 = MapUtlis.getNew(lDNComity100, lDBComity100, lDAComity100);

            //播报
            if (lDNComity20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNComity20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_rr_20));
            }
            if (lDNComity100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNComity100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_rr_100));
            }
            //计算完毕放入历史数据
            lDAComity20 = inputOldData(lat, lon, lDAComity20, lDBComity20, PublicApplication.longTwo);
            lDAComity100 = inputOldData(lat, lon, lDAComity100, lDBComity100, PublicApplication.longOne);
            /*lDAComity20.clear();
            lDAComity20.addAll(lDBComity20);
            lDAComity100.clear();
            lDAComity100.addAll(lDBComity100);*/
        }
    }

    /**
     * 压实线违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanYSX(String lat, String lon) {
        lDBYSX100.clear();
        lDNYSX100.clear();
        lDBYSX20.clear();
        lDNYSX20.clear();
        lDBYSX100 = MapUtlis.getCoordinate100(lat, lon, 2);
        lDBYSX20.clear();
        lDBYSX20.addAll(lDBYSX100);
        if (lDBYSX100 != null) {
            if (isFirst) {
                lDBYSX100 = MapUtlis.getB(lat, lon, lDBYSX100, PublicApplication.longTwo - 1);
                lDBYSX100 = MapUtlis.jisuanFangXiang(lDBYSX100, lat, lon);
                if (lDBYSX100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBYSX100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《压实线违章》抓拍");
                }//计算完毕放入历史数据
                lDAYSX100.clear();
                lDAYSX100.addAll(lDBYSX100);
                lDAYSX20.clear();
                lDAYSX20.addAll(lDBYSX100);
                return;
            }
            //周围坐标
            lDBYSX20 = MapUtlis.getB(lat, lon, lDBYSX20, PublicApplication.longOne);
            lDBYSX100 = MapUtlis.getB(lat, lon, lDBYSX100, PublicApplication.longTwo);
            //计算方向
            lDBYSX20 = MapUtlis.jisuanFangXiang(lDBYSX20, lat, lon);
            lDBYSX100 = MapUtlis.jisuanFangXiang(lDBYSX100, lat, lon);

            //移动方向内的点
            lDBYSX20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBYSX20);
            lDBYSX100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBYSX100);
            //新坐标
            lDNYSX20 = MapUtlis.getNew(lDNYSX20, lDBYSX20, lDAYSX20);
            lDNYSX100 = MapUtlis.getNew(lDNYSX100, lDBYSX100, lDAYSX100);
            //播报
            if (lDNYSX20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNYSX20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_sx_20));
            }
            if (lDNYSX100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNYSX100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_sx_100));
            }
            //计算完毕放入历史数据
            lDAYSX20 = inputOldData(lat, lon, lDAYSX20, lDBYSX20, PublicApplication.longTwo);
            lDAYSX100 = inputOldData(lat, lon, lDAYSX100, lDBYSX100, PublicApplication.longOne);
           /* //计算完毕放入历史数据
            lDAYSX20.clear();
            lDAYSX20.addAll(lDBYSX20);
            lDAYSX100.clear();
            lDAYSX100.addAll(lDBYSX100);*/
        }
    }

    /**
     * 黄网格违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanHWG(String lat, String lon) {
        lDBHWG100.clear();
        lDNHWG100.clear();
        lDBHWG20.clear();
        lDNHWG20.clear();
        lDBHWG100 = MapUtlis.getCoordinate100(lat, lon, 3);
        lDBHWG20.clear();
        lDBHWG20.addAll(lDBHWG100);
        if (lDBHWG100 != null) {
            if (isFirst) {
                lDBHWG100 = MapUtlis.getB(lat, lon, lDBHWG100, PublicApplication.longTwo - 1);
                lDBHWG100 = MapUtlis.jisuanFangXiang(lDBHWG100, lat, lon);
                if (lDBHWG100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBHWG100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《黄色网格违章》抓拍");
                }//计算完毕放入历史数据
                lDAHWG100.clear();
                lDAHWG100.addAll(lDBHWG100);
                lDAHWG20.clear();
                lDAHWG20.addAll(lDBHWG100);
                return;
            }
            //周围坐标
            lDBHWG20 = MapUtlis.getB(lat, lon, lDBHWG20, PublicApplication.longOne);
            lDBHWG100 = MapUtlis.getB(lat, lon, lDBHWG100, PublicApplication.longTwo);
            //计算方向
            lDBHWG20 = MapUtlis.jisuanFangXiang(lDBHWG20, lat, lon);
            lDBHWG100 = MapUtlis.jisuanFangXiang(lDBHWG100, lat, lon);
            //移动方向内的点
            lDBHWG20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBHWG20);
            lDBHWG100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBHWG100);
            //新坐标
            lDNHWG20 = MapUtlis.getNew(lDNHWG20, lDBHWG20, lDAHWG20);
            lDNHWG100 = MapUtlis.getNew(lDNHWG100, lDBHWG100, lDAHWG100);
            //播报
            if (lDNHWG20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNHWG20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_hw_20));
            }
            if (lDNHWG100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNHWG100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_hw_100));
            }
            //计算完毕放入历史数据
            lDAHWG20 = inputOldData(lat, lon, lDAHWG20, lDBHWG20, PublicApplication.longTwo);
            lDAHWG100 = inputOldData(lat, lon, lDAHWG100, lDBHWG100, PublicApplication.longOne);
            /*//计算完毕放入历史数据
            lDAHWG20.clear();
            lDAHWG20.addAll(lDBHWG20);
            lDAHWG100.clear();
            lDAHWG100.addAll(lDBHWG100);*/

        }
    }

    /**
     * 直行道掉头违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanDT(String lat, String lon) {
        lDBDT100.clear();
        lDNDT100.clear();
        lDBDT20.clear();
        lDNDT20.clear();
        lDBDT100 = MapUtlis.getCoordinate100(lat, lon, 4);
        lDBDT20.clear();
        lDBDT20.addAll(lDBDT100);
        if (lDBDT100 != null) {
            if (isFirst) {
                lDBDT100 = MapUtlis.getB(lat, lon, lDBDT100, PublicApplication.longTwo - 1);
                lDBDT100 = MapUtlis.jisuanFangXiang(lDBDT100, lat, lon);
                if (lDBDT100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBDT100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《直行道掉头违章》抓拍");
                } //计算完毕放入历史数据
                lDADT100.clear();
                lDADT100.addAll(lDBDT100);
                lDADT20.clear();
                lDADT20.addAll(lDBDT100);
                return;
            }
            //周围坐标
            lDBDT20 = MapUtlis.getB(lat, lon, lDBDT20, PublicApplication.longOne);
            lDBDT100 = MapUtlis.getB(lat, lon, lDBDT100, PublicApplication.longTwo);
            //计算方向
            lDBDT20 = MapUtlis.jisuanFangXiang(lDBDT20, lat, lon);
            lDBDT100 = MapUtlis.jisuanFangXiang(lDBDT100, lat, lon);
            //移动方向内的点
            lDBDT20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBDT20);
            lDBDT100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBDT100);
            //新坐标
            lDNDT20 = MapUtlis.getNew(lDNDT20, lDBDT20, lDADT20);
            lDNDT100 = MapUtlis.getNew(lDNDT100, lDBDT100, lDADT100);
            //播报
            if (lDNDT20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNDT20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_dt_20));
            }
            if (lDNDT100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNDT100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_dt_100));
            }
            //计算完毕放入历史数据
            lDADT20 = inputOldData(lat, lon, lDADT20, lDBDT20, PublicApplication.longTwo);
            lDADT100 = inputOldData(lat, lon, lDADT100, lDBDT100, PublicApplication.longOne);
            /*//计算完毕放入历史数据
            lDADT20.clear();
            lDADT20.addAll(lDBDT20);
            lDADT100.clear();
            lDADT100.addAll(lDBDT100);*/

        }

    }

    /**
     * 鸣笛违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanMD(String lat, String lon) {
        lDBMD100.clear();
        lDNMD100.clear();
        lDBMD20.clear();
        lDNMD20.clear();
        lDBMD100 = MapUtlis.getCoordinate100(lat, lon, 5);
        lDBMD20.clear();
        lDBMD20.addAll(lDBMD100);
        if (lDBMD100 != null) {
            if (isFirst) {
                lDBMD100 = MapUtlis.getB(lat, lon, lDBMD100, PublicApplication.longTwo - 1);
                lDBMD100 = MapUtlis.jisuanFangXiang(lDBMD100, lat, lon);
                if (lDBMD100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBMD100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《鸣笛违章》抓拍");
                } //计算完毕放入历史数据
                lDAMD100.clear();
                lDAMD100.addAll(lDBMD100);
                lDAMD20.clear();
                lDAMD20.addAll(lDBMD100);
                return;
            }
            //周围坐标
            lDBMD20 = MapUtlis.getB(lat, lon, lDBMD20, PublicApplication.longOne);
            lDBMD100 = MapUtlis.getB(lat, lon, lDBMD100, PublicApplication.longTwo);
            //计算方向
            lDBMD20 = MapUtlis.jisuanFangXiang(lDBMD20, lat, lon);
            lDBMD100 = MapUtlis.jisuanFangXiang(lDBMD100, lat, lon);
            //移动方向内的点
            lDBMD20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBMD20);
            lDBMD100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBMD100);
            //新坐标
            lDNMD20 = MapUtlis.getNew(lDNMD20, lDBMD20, lDAMD20);
            lDNMD100 = MapUtlis.getNew(lDNMD100, lDBMD100, lDAMD100);
            //播报
            if (lDNMD20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNMD20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_md_20));
            }
            if (lDNMD100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNMD100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_md_100));
            }
            //计算完毕放入历史数据
            lDAMD20 = inputOldData(lat, lon, lDAMD20, lDBMD20, PublicApplication.longTwo);
            lDAMD100 = inputOldData(lat, lon, lDAMD100, lDBMD100, PublicApplication.longOne);
            /*//计算完毕放入历史数据
            lDAMD20.clear();
            lDAMD20.addAll(lDBMD20);
            lDAMD100.clear();
            lDAMD100.addAll(lDBMD100);*/

        }
    }

    /**
     * 远光灯违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanYGD(String lat, String lon) {
        lDBYGD100.clear();
        lDNYGD100.clear();
        lDBYGD20.clear();
        lDNYGD20.clear();
        lDBYGD100 = MapUtlis.getCoordinate100(lat, lon, 6);
        lDBYGD20.clear();
        lDBYGD20.addAll(lDBYGD100);
        if (lDBYGD100 != null) {
            if (isFirst) {
                lDBYGD100 = MapUtlis.getB(lat, lon, lDBYGD100, PublicApplication.longTwo - 1);
                lDBYGD100 = MapUtlis.jisuanFangXiang(lDBYGD100, lat, lon);
                if (lDBYGD100.size() > 0) {
                    String str = MapUtlis.getFXStr(lDBYGD100);
                    PublicApplication.mVoiceManage.addVoice20("您的" + str + "100米内有《远光灯违章》抓拍");
                }
                //计算完毕放入历史数据
                lDAYGD100.clear();
                lDAYGD100.addAll(lDBYGD100);
                lDAYGD20.clear();
                lDAYGD20.addAll(lDBYGD100);
                return;
            }
            //周围坐标
            lDBYGD20 = MapUtlis.getB(lat, lon, lDBYGD20, PublicApplication.longOne);
            lDBYGD100 = MapUtlis.getB(lat, lon, lDBYGD100, PublicApplication.longTwo);
            //计算方向
            lDBYGD20 = MapUtlis.jisuanFangXiang(lDBYGD20, lat, lon);
            lDBYGD100 = MapUtlis.jisuanFangXiang(lDBYGD100, lat, lon);
            //移动方向内的点
            lDBYGD20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBYGD20);
            lDBYGD100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBYGD100);
            //新坐标
            lDNYGD20 = MapUtlis.getNew(lDNYGD20, lDBYGD20, lDAYGD20);
            lDNYGD100 = MapUtlis.getNew(lDNYGD100, lDBYGD100, lDAYGD100);
            //播报
            if (lDNYGD20.size() > 0) {
                String str = MapUtlis.getFXStr(lDNYGD20);
                PublicApplication.mVoiceManage.addVoice20("前方" + str + getResources().getString(R.string.wz_yg_20));
            }
            if (lDNYGD100.size() > 0) {
                String str = MapUtlis.getFXStr(lDNYGD100);
                PublicApplication.mVoiceManage.addVoice100("前方" + str + getResources().getString(R.string.wz_yg_100));
            }
            //计算完毕放入历史数据
            lDAYGD20 = inputOldData(lat, lon, lDAYGD20, lDBYGD20, PublicApplication.longTwo);
            lDAYGD100 = inputOldData(lat, lon, lDAYGD100, lDBYGD100, PublicApplication.longOne);
           /* //计算完毕放入历史数据
            lDAYGD20.clear();
            lDAYGD20.addAll(lDBYGD20);
            lDAYGD100.clear();
            lDAYGD100.addAll(lDBYGD100);*/
        }
    }

    /**
     * 特殊类型违章计算
     *
     * @param lat
     * @param lon
     */
    private synchronized void jisuanTS(String lat, String lon) {
        lDBTS100.clear();
        lDNTS100.clear();
        lDBTS20.clear();
        lDNTS20.clear();
        lDBTS100 = MapUtlis.getCoordinate100(lat, lon, 8);
        lDBTS20.clear();
        lDBTS20.addAll(lDBTS100);
        if (lDBTS100 != null) {
            if (isFirst) {
                lDBTS100 = MapUtlis.getB(lat, lon, lDBTS100, PublicApplication.longTwo - 1);
                for (int i = 0; i < lDBTS100.size(); i++) {
                    if (!StrUtils.isEmpty(lDBTS100.get(i).getDenoter_name())) {
                        PublicApplication.mVoiceManage.addVoice20("您的100米内有" + lDBTS100.get(i).getDenoter_name());
                    }
                }
                //计算完毕放入历史数据
                lDATS100.clear();
                lDATS100.addAll(lDBTS100);
                lDATS20.clear();
                lDATS20.addAll(lDBTS100);
                return;
            }
            //周围坐标
            lDBTS20 = MapUtlis.getB(lat, lon, lDBTS20, PublicApplication.longOne);
            lDBTS100 = MapUtlis.getB(lat, lon, lDBTS100, PublicApplication.longTwo);
            //计算方向
            lDBTS20 = MapUtlis.jisuanFangXiang(lDBTS20, lat, lon);
            lDBTS100 = MapUtlis.jisuanFangXiang(lDBTS100, lat, lon);
            //移动方向内的点
            lDBTS20 = MapUtlis.getSameDirection(ydJiao, p_jiao_20, lDBTS20);
            lDBTS100 = MapUtlis.getSameDirection(ydJiao, p_jiao_100, lDBTS100);
            //新坐标
            lDNTS20 = MapUtlis.getNew(lDNTS20, lDBTS20, lDATS20);
            lDNTS100 = MapUtlis.getNew(lDNTS100, lDBTS100, lDATS100);
            //播报
            for (int i = 0; i < lDNTS20.size(); i++) {
                if (!StrUtils.isEmpty(lDNTS20.get(i).getDenoter_name()))
                    PublicApplication.mVoiceManage.addVoice20("前方" + lDNTS20.get(i).getDenoter_name());
            }
            for (int i = 0; i < lDNTS100.size(); i++) {
                if (!StrUtils.isEmpty(lDNTS100.get(i).getDenoter_name()))
                    PublicApplication.mVoiceManage.addVoice100("前方" + lDNTS100.get(i).getDenoter_name());
            }
            //计算完毕放入历史数据
            lDATS20 = inputOldData(lat, lon, lDATS20, lDBTS20, PublicApplication.longTwo);
            lDATS100 = inputOldData(lat, lon, lDATS100, lDBTS100, PublicApplication.longOne);
            /*//计算完毕放入历史数据
            lDATS20.clear();
            lDATS20.addAll(lDBTS20);
            lDATS100.clear();
            lDATS100.addAll(lDBTS100);*/
        }
    }

    /**
     * 禁止停车违章计算
     *
     * @param lat
     * @param lon
     * @param aMap
     */
    private void jisuanNoStop(String lat, String lon, AMap aMap) {
        lDNNoStop = MapUtlis.getCoordinate100(lat, lon, 7, MapUtlis.timedate((System.currentTimeMillis() / 1000) + ""));
        if (lDNNoStop.size() > 0) {
            LatLng arg0 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
//            aMap.clear(true);
            for (int i = 0; i < lDNNoStop.size(); i++) {
                //禁停区域绘制
                PolygonOptions pOption = new PolygonOptions();
                if (lDNNoStop.get(i).getListLatlon() != null
                        && lDNNoStop.get(i).getListLatlon().size() >= 4) {
                    for (int j = 0; j < lDNNoStop.get(i).getListLatlon().size(); j++) {
                        pOption.add(lDNNoStop.get(i).getListLatlon().get(j));
                    }
                    polygon = aMap.addPolygon(pOption
                            .visible(false));
                    /*polygon = aMap.addPolygon(pOption.strokeWidth(4)
                            .strokeColor(Color.argb(50, 1, 1, 1))
                            .fillColor(Color.argb(50, 1, 1, 1)));*/
                }
                boolean b1 = false;
                if (polygon != null)
                    b1 = polygon.contains(arg0);
                if (b1)
                    PublicApplication.mVoiceManage.addVoice20(getResources().getString(R.string.wz_jt));
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {
                PublicApplication.mVoiceManage.addVoice20("灵狗持续为您服务！");
            }
        });
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 历史数据储存
     *
     * @param lat
     * @param lon
     * @param lDA
     * @param lDB
     * @param mLong
     * @return
     */
    private List<CoordinateBean> inputOldData(
            String lat, String lon,
            List<CoordinateBean> lDA,
            List<CoordinateBean> lDB,
            double mLong) {
        lDA = MapUtlis.getB(lat, lon, lDA, mLong);
        lDA.addAll(lDB);
        return lDA;
    }
}
