package com.soowin.cleverdog.utlis;

import android.database.Cursor;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.info.service.CoordinateBean;
import com.soowin.cleverdog.info.service.NoStoppingBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hxt on 2017/8/31.
 */

public class MapUtlis {
    /*private static double EARTH_RADIUS = 6378.137;// 单位千米

    *//**
     * 角度弧度计算公式 rad:(). <br/>
     * <p>
     * 360度=2π π=Math.PI
     * <p>
     * x度 = x*π/360 弧度
     *
     * @param
     * @return
     * @author chiwei
     * @since JDK 1.6
     *//*
    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    *//**
     * 根据经纬度计算两点之间的距离 GetDistance:(). <br/>
     *
     * @param slat1 1点的纬度
     * @param slng1 1点的经度
     * @param slat2 2点的纬度
     * @param slng2 2点的经度
     * @return 距离 单位 米
     * @author chiwei
     * @since JDK 1.6
     *//*
    public static double getDistance(String slat1, String slng1, String slat2, String slng2) {
        double lat1 = Double.parseDouble(slat1);
        double lng1 = Double.parseDouble(slng1);
        double lat2 = Double.parseDouble(slat2);
        double lng2 = Double.parseDouble(slng2);
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }*/
//    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
//        double radLat1 = getRadian(lat1);
//        double radLat2 = getRadian(lat2);
//        double a = radLat1 - radLat2;// 两点纬度差
//        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
//        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
//                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
//        s = s * EARTH_RADIUS;
//        return s * 1000;
//    }

    /**
     * 计算距离
     *
     * @param lat1 原始坐标
     * @param lng1
     * @param lat2 新坐标
     * @param lng2
     * @return
     */
    public static double getDistance(String lat1, String lng1, String lat2, String lng2) {
        LatLng latLng1 = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
        LatLng latLng2 = new LatLng(Double.parseDouble(lat2), Double.parseDouble(lng2));
        double distance = AMapUtils.calculateLineDistance(latLng1, latLng2);
        return distance;
    }

    /**
     * 获取两点的角度
     *
     * @return
     */
    public static double getDirection(String lat, String lon, String lat2, String lon2) {
        if (StrUtils.isEmpty(lat) || StrUtils.isEmpty(lon) || StrUtils.isEmpty(lat2) || StrUtils.isEmpty(lon2)) {
            return 0.0;
        }
        double x1 = Double.parseDouble(lat);
        double x2 = Double.parseDouble(lat2);
        double y1 = Double.parseDouble(lon);
        double y2 = Double.parseDouble(lon2);

        double y = Math.sin(y2 - y1) * Math.cos(x2);
        double x = Math.cos(x1) * Math.sin(x2) - Math.sin(x1) * Math.cos(x2) * Math.cos(y2 - y1);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        if (brng < 0)
            brng = brng + 360;
        return brng;
    }
    /**
     * 获取两点的角度
     *
     * @return
     *//*
    public static double getDirection(String x1s, String y1s, String x2s, String y2s, AMap aMap) {
        if (StrUtils.isEmpty(x1s) || StrUtils.isEmpty(y1s) || StrUtils.isEmpty(x2s) || StrUtils.isEmpty(y2s)) {
            return 0.0;
        }
        double x1 = Double.parseDouble(x1s);
        double x2 = Double.parseDouble(x2s);
        double y1 = Double.parseDouble(y1s);
        double y2 = Double.parseDouble(y2s);

        *//*double a = x2 - x1;
        double b = y2 - y1;
        double direction = (double) (Math.atan2(a, b) / Math.PI * 180);

        if (direction < 0)
            direction = 360 + direction;
        return direction;*//*

        LatLng startPoint = new LatLng(x1,y1);
        LatLng endPoint = new LatLng(x2,y2);
        return getAngle(startPoint, endPoint);
    }*/

    /**
     * 获取百米范围内的坐标点
     * type 1、车让人违章，2、轧实线违章，3、黄色网格违章，4、直行道掉头违章，5、鸣笛违章，6、远光灯违章
     */
    public static List<CoordinateBean> getCoordinate100(String lat, String lon, int denoter_type) {
        double flat = Double.parseDouble(lat);
        double flon = Double.parseDouble(lon);
        double plat = Double.parseDouble(PublicApplication.getContext().getString(R.string.latitude_100));
        double plon = Double.parseDouble(PublicApplication.getContext().getString(R.string.longitude_100));
        String[] sqlData = {};
        Cursor cursor = PublicApplication.db.find(
                "select * from main_table where " +
                        "(latitude BETWEEN " + (flat - plat)
                        + " and " + (flat + plat) +
                        ") and (longitude BETWEEN " + (flon - plon)
                        + " and " + (flon + plon) + ") " +
                        " and denoter_type = " + denoter_type, sqlData);

        List<CoordinateBean> dataList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CoordinateBean coordinateBean = new CoordinateBean();
            coordinateBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            coordinateBean.setDenoter_id(cursor.getInt(cursor.getColumnIndex("denoter_id")));
            coordinateBean.setLat(cursor.getString(cursor.getColumnIndex("latitude")));
            coordinateBean.setLon(cursor.getString(cursor.getColumnIndex("longitude")));
            coordinateBean.setDenoter_name(cursor.getString(cursor.getColumnIndex("denoter_name")));
            coordinateBean.setDenoter_type(cursor.getString(cursor.getColumnIndex("denoter_type")));
            coordinateBean.setDirection(cursor.getString(cursor.getColumnIndex("direction")));
            coordinateBean.setStar_time(cursor.getString(cursor.getColumnIndex("star_time")));
            coordinateBean.setEnd_time(cursor.getString(cursor.getColumnIndex("end_time")));
            dataList.add(coordinateBean);
            cursor.moveToNext();
        }
        return dataList;
    }

    /**
     * 获取百米范围内的坐标点
     * type 1、车让人违章，2、轧实线违章，3、黄色网格违章，4、直行道掉头违章，5、鸣笛违章，6、远光灯违章
     */
    public static List<CoordinateBean> getCoordinate100() {
        double flat = 38.0446311648;
        double flon = 114.6151793003;
        double plat = Double.parseDouble(10 + "");
        double plon = Double.parseDouble(10 + "");
        String[] sqlData = {};
        Cursor cursor = PublicApplication.db.find(
                "select * from main_table where " +
                        "(latitude BETWEEN " + (flat - plat)
                        + " and " + (flat + plat) +
                        ") and (longitude BETWEEN " + (flon - plon)
                        + " and " + (flon + plon) + ") and (denoter_type < 7 or denoter_type > 7)", sqlData);

        List<CoordinateBean> dataList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CoordinateBean coordinateBean = new CoordinateBean();
            coordinateBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            coordinateBean.setDenoter_id(cursor.getInt(cursor.getColumnIndex("denoter_id")));
            coordinateBean.setLat(cursor.getString(cursor.getColumnIndex("latitude")));
            coordinateBean.setLon(cursor.getString(cursor.getColumnIndex("longitude")));
            coordinateBean.setDenoter_name(cursor.getString(cursor.getColumnIndex("denoter_name")));
            coordinateBean.setDenoter_type(cursor.getString(cursor.getColumnIndex("denoter_type")));
            coordinateBean.setDirection(cursor.getString(cursor.getColumnIndex("direction")));
            coordinateBean.setStar_time(cursor.getString(cursor.getColumnIndex("star_time")));
            coordinateBean.setEnd_time(cursor.getString(cursor.getColumnIndex("end_time")));
            dataList.add(coordinateBean);
            cursor.moveToNext();
        }
        cursor.close();
        return dataList;
    }

    /**
     * 获取20米范围内的坐标点
     * type 1、车让人违章，2、轧实线违章，3、黄色网格违章，4、直行道掉头违章，5、鸣笛违章，6、远光灯违章
     */
    public static List<CoordinateBean> getCoordinate20(String lat, String lon, int denoter_type) {
        double flat = Double.parseDouble(lat);
        double flon = Double.parseDouble(lon);
        double plat = Double.parseDouble(PublicApplication.getContext().getString(R.string.latitude_20));
        double plon = Double.parseDouble(PublicApplication.getContext().getString(R.string.longitude_20));
        String[] sqlData = {};
        Cursor cursor = PublicApplication.db.find(
                "select * from main_table where " +
                        "(latitude BETWEEN " + (flat - plat)
                        + " and " + (flat + plat) +
                        ") and (longitude BETWEEN " + (flon - plon)
                        + " and " + (flon + plon) + ") " +
                        " and denoter_type = " + denoter_type, sqlData);

        List<CoordinateBean> dataList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CoordinateBean coordinateBean = new CoordinateBean();
            coordinateBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            coordinateBean.setDenoter_id(cursor.getInt(cursor.getColumnIndex("denoter_id")));
            coordinateBean.setLat(cursor.getString(cursor.getColumnIndex("latitude")));
            coordinateBean.setLon(cursor.getString(cursor.getColumnIndex("longitude")));
            coordinateBean.setDenoter_name(cursor.getString(cursor.getColumnIndex("denoter_name")));
            coordinateBean.setDenoter_type(cursor.getString(cursor.getColumnIndex("denoter_type")));
            coordinateBean.setDirection(cursor.getString(cursor.getColumnIndex("direction")));
            coordinateBean.setStar_time(cursor.getString(cursor.getColumnIndex("star_time")));
            coordinateBean.setEnd_time(cursor.getString(cursor.getColumnIndex("end_time")));
            dataList.add(coordinateBean);
            cursor.moveToNext();
        }
        cursor.close();
        return dataList;
    }

    /**
     * 获取10千米范围内的禁停坐标点
     */
    public static List<NoStoppingBean> getCoordinate100(String lat, String lon, int denoter_type, String time) {
        double flat = Double.parseDouble(lat);
        double flon = Double.parseDouble(lon);
        double plat = Double.parseDouble(PublicApplication.getContext().getString(R.string.latitude_10k));
        double plon = Double.parseDouble(PublicApplication.getContext().getString(R.string.longitude_10k));
        String[] sqlData = {};
        Cursor cursor = PublicApplication.db.find(
                "select denoter_id from main_table where " +
                        "(latitude BETWEEN " + (flat - plat)
                        + " and " + (flat + plat) +
                        ") and (longitude BETWEEN " + (flon - plon)
                        + " and " + (flon + plon) + ") " +
                        " and denoter_type = " + denoter_type + " and '" +
                        time + "' BETWEEN star_time " +
                        "and end_time order by id asc", sqlData);

        List<Integer> dataList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            dataList.add(cursor.getInt(cursor.getColumnIndex("denoter_id")));
            cursor.moveToNext();
        }
        cursor.close();
        HashSet h = new HashSet(dataList);
        dataList.clear();
        dataList.addAll(h);
        List<NoStoppingBean> dataNoStoppingBean = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            NoStoppingBean noStoppingBean = new NoStoppingBean();
            List<LatLng> listLatLng = new ArrayList<>();
            Cursor cursor1 = PublicApplication.db.find(
                    "select * from main_table where denoter_id = " + dataList.get(i) + " order by id asc", sqlData);
            cursor1.moveToFirst();
            for (int j = 0; j < cursor1.getCount(); j++) {
                String latc = cursor1.getString(cursor1.getColumnIndex("latitude"));
                String lngc = cursor1.getString(cursor1.getColumnIndex("longitude"));
                LatLng latlng = new LatLng(Double.parseDouble(latc), Double.parseDouble(lngc));
                listLatLng.add(latlng);
                noStoppingBean.setDenoter_id(cursor1.getInt(cursor1.getColumnIndex("denoter_id")));
                noStoppingBean.setListLatlon(listLatLng);
                cursor1.moveToNext();
            }
            cursor1.close();
            dataNoStoppingBean.add(noStoppingBean);
        }
        return dataNoStoppingBean;
    }

    /**
     * 计算并存储方向 为汉字格式
     */
    public static List<CoordinateBean> jisuanFangXiang(
            List<CoordinateBean> listDataA, String lat, String lon) {
        for (int i = 0; i < listDataA.size(); i++) {
            double jiao = MapUtlis.getDirection(lat, lon,
                    listDataA.get(i).getLat(),
                    listDataA.get(i).getLon());
            listDataA.get(i).setDirection(jiao + "");
            if (337.5 <= jiao || 22.5 >= jiao) {
                listDataA.get(i).setDirection_name("正北");
            } else if (22.5 < jiao && 67.5 > jiao) {
                listDataA.get(i).setDirection_name("东北");
            } else if (67.5 <= jiao && 112.5 >= jiao) {
                listDataA.get(i).setDirection_name("正东");
            } else if (112.5 < jiao && 157.5 > jiao) {
                listDataA.get(i).setDirection_name("东南");
            } else if (157.5 <= jiao && 202.5 >= jiao) {
                listDataA.get(i).setDirection_name("正南");
            } else if (202.5 < jiao && 247.5 > jiao) {
                listDataA.get(i).setDirection_name("西南");
            } else if (247.5 <= jiao && 292.5 >= jiao) {
                listDataA.get(i).setDirection_name("正西");
            } else if (292.5 < jiao && 337.5 > jiao) {
                listDataA.get(i).setDirection_name("西北");
            }
        }
        return listDataA;
    }

    /**
     * 计算并存储方向 为数字格式
     *//*
    public static List<CoordinateBean> jisuanFangXiang1(
            List<CoordinateBean> listDataA, String lat, String lon) {
        for (int i = 0; i < listDataA.size(); i++) {
            double jiao = MapUtlis.getDirection(lat, lon,
                    listDataA.get(i).getLat(),
                    listDataA.get(i).getLon());
            listDataA.get(i).setDirection(jiao + "");
        }
        return listDataA;
    }*/

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timedate(String time) {
        if (!StrUtils.isEmpty(time)) {
            SimpleDateFormat sdr = new SimpleDateFormat("HH:mm:ss");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            return times;
        } else {
            return "";
        }
    }

    /**
     * 获取最新的点位(与旧去重)
     *
     * @param mNew
     * @param mB
     * @param mOld
     * @return
     */
    public static List<CoordinateBean> getNew(
            List<CoordinateBean> mNew,
            List<CoordinateBean> mB,
            List<CoordinateBean> mOld) {
        mNew.clear();
        mNew.addAll(mB);
        if (mNew.size() > 0 && mOld.size() > 0)
            for (int i = mOld.size() - 1; i >= 0; i--) {
                for (int j = mNew.size() - 1; j >= 0; j--) {
                    if (mNew.get(j).getId() == mOld.get(i).getId())
                        mNew.remove(j);
                }
            }
        HashSet h = new HashSet(mNew);
        mNew.clear();
        mNew.addAll(h);
        return mNew;
    }

    /**
     * 获取b点周围坐标点
     *
     * @return
     */
    public static List<CoordinateBean> getB(
            String lat, String lon, List<CoordinateBean> mb, double mLong) {
        if (mb.size() > 0)
            for (int i = mb.size() - 1; i >= 0; i--) {
                Double toLong = MapUtlis.getDistance(lat, lon,
                        mb.get(i).getLat(), mb.get(i).getLon());
                if (mLong == PublicApplication.longTwo) {
                    if (toLong <= PublicApplication.longOne)
                        mb.remove(i);
                }
                if (toLong > mLong)
                    mb.remove(i);
            }
        return mb;
    }

    /**
     * 获取与我方向一致的点
     *
     * @param yd_jiao 运动角度
     * @param jiao_p  偏移量
     * @return
     */
    public static List<CoordinateBean> getSameDirection(double yd_jiao, double jiao_p, List<CoordinateBean> listData) {
        if (listData.size() > 0)
            for (int i = listData.size() - 1; i >= 0; i--) {
                double xd_jiao = Double.parseDouble(listData.get(i).getDirection());//坐标与标志位相对角度
                if (yd_jiao < jiao_p) {//运动角度小与偏移量时
                    if (xd_jiao <= (yd_jiao + jiao_p)) {
                    } else if (xd_jiao >= (360 - yd_jiao)) {
                    } else {
                        listData.remove(i);
                    }
                } else if (yd_jiao > (360 - jiao_p)) {//运动角度加偏移量大于360度时
                    if (xd_jiao >= (yd_jiao - jiao_p)) {
                    } else if (xd_jiao <= (yd_jiao + jiao_p - 360)) {
                    } else {
                        listData.remove(i);
                    }

                } else {//运动角度在正负偏移量中间时
                    if (xd_jiao >= (yd_jiao - jiao_p) && xd_jiao <= (yd_jiao + jiao_p)) {
                    } else {
                        listData.remove(i);
                    }
                }
            }
        return listData;
    }

    /**
     * 获取语音播报方向集合
     */
    public static String getFXStr(List<CoordinateBean> mNew) {
        List<String> fx_str_list = new ArrayList<>();
        for (int i = 0; i < mNew.size(); i++) {
            fx_str_list.add(mNew.get(i).getDirection_name());
        }
        HashSet h = new HashSet(fx_str_list);
        fx_str_list.clear();
        fx_str_list.addAll(h);

        String speakStr = "";
        for (int i = 0; i < fx_str_list.size(); i++) {
            if (i > 0)
                speakStr = speakStr + "," + fx_str_list.get(i);
            else
                speakStr = fx_str_list.get(i);
            if (i == fx_str_list.size())
                if (!StrUtils.isEmpty(fx_str_list.get(i)))
                    speakStr = speakStr + "方向";
        }
        return speakStr;
    }
}
