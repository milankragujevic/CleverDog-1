package com.soowin.cleverdog.activity.index;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;

public class MapActivity extends BaseActivity {
    public static final String TAG = MapActivity.class.getSimpleName();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Log.e(TAG, "onLocationChanged: "
                            + "--方向：" + aMapLocation.getBearing()
                            + "--精度：" + aMapLocation.getGpsAccuracyStatus()
                            + "--速度：" + aMapLocation.getSpeed()
                            + "--卫星数量：" + aMapLocation.getSatellites()
                            + "--卫星信号强度：" + aMapLocation.getGpsAccuracyStatus()
                            + "--位置质量：" + aMapLocation.getLocationQualityReport());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    if (aMapLocation.getErrorCode() == 12) {
                        showToast("请在设备的设置中开启app的定位权限。");
                    }
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initAMap();
        initShowPrompt();
    }

    private void initShowPrompt() {
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent mIntent = new Intent();
//        mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
//        mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
//        startService(mIntent);

        mIntent.setClass(PublicApplication.getContext(),MainActivity.class);

        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, mIntent, 0);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),10*1000,pi);
    }

    private void initAMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(8000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
}
