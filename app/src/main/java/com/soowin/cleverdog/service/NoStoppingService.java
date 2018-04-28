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
import com.soowin.cleverdog.info.service.NoStoppingBean;
import com.soowin.cleverdog.utlis.MapUtlis;
import com.soowin.cleverdog.utlis.PublicApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxt on 2017/9/2.
 * 禁止停车服务
 */

public class NoStoppingService extends Service {
    public static final String TAG = "com.soowin.cleverdog.service.NoStoppingService";

    private List<NoStoppingBean> listDataNew = new ArrayList<>();
    private Polygon polygon;

    private NoStoppingService.MyBinder mBinder = new NoStoppingService.MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class MyBinder extends Binder {
        public void starNoStop(final String lat, final String lon, final AMap aMap) {
            new Thread() {
                @Override
                public void run() {
                    jisuanNoStop(lat, lon, aMap);
                }
            }.start();
        }
    }

    private void jisuanNoStop(String lat, String lon, AMap aMap) {
        listDataNew = MapUtlis.getCoordinate100(lat,lon,7,"");
        if (listDataNew.size() > 0) {
            LatLng arg0 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            for (int i = 0; i < listDataNew.size(); i++) {
                //禁停区域绘制
                PolygonOptions pOption = new PolygonOptions();
                for (int j = 0; j < listDataNew.get(i).getListLatlon().size(); j++) {
                    pOption.add(listDataNew.get(i).getListLatlon().get(j));
                }
                polygon = aMap.addPolygon(pOption
                        .visible(false));
            }
            boolean b1 = polygon.contains(arg0);
            if (b1)
                PublicApplication.mVoiceManage.addVoice100(getResources().getString(R.string.wz_jt));
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
/*pOption.add(new LatLng(38.0445260000, 114.6139480000));
                pOption.add(new LatLng(38.0445340000, 114.6138400000));
                pOption.add(new LatLng(38.0441840000, 114.6138460000));
                pOption.add(new LatLng(38.0441840000, 114.6139690000));
                polygon = aMap.addPolygon(pOption.strokeWidth(4)
                    .strokeColor(Color.argb(50, 1, 1, 1))
                    .fillColor(Color.argb(50, 1, 1, 1))
                    .visible(true));*/