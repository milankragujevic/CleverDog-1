package com.soowin.cleverdog.activity.index;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceStatusListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.login.LoginActivity;
import com.soowin.cleverdog.activity.welcome.WelcomeActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.BaseBean;
import com.soowin.cleverdog.info.login.LoginBean;
import com.soowin.cleverdog.info.service.CoordinateBean;
import com.soowin.cleverdog.service.ComityService;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.MapUtlis;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.Utlis;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AMapLocationListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    //高德地图
    private MapView mMapView;
    private AMap aMap;
    private double mYDJiao;
    private double mJPJiao;//轨迹纠偏后的角度
    private boolean isCaiYong = true;//是否采用了上一个点
    LBSTraceClient lbsTraceClient;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private boolean isFirst = true;//是否为第一次播放

    private String lata = "0";
    private String lona = "0";

    private int width;
    //页面布局top
    private RelativeLayout rlTop;
    private ImageView ivLeiDa;
    private ListView lvVoice;
    private TextView tvGpsRuo;
    //页面布局bottom
    private RelativeLayout rlBottom;
    private TextView tvSetting;
    private ImageView ivBiaoZhen;
    private Bitmap bit;
    private ImageButton ibTopLine;
    private ImageButton ibShowTicket;
    private TextView tvMe;
    private TextView tvShare;

    private int bzWidth;
    private int bzHeight;
    //设置页面
    private CheckBox cbCrr;
    private CheckBox cbYsx;
    private CheckBox cbHwg;
    private CheckBox cbDt;
    private CheckBox cbMd;
    private CheckBox cbYgd;
    private CheckBox cbJt;
    private TextView tvSubmit;
    private DrawerLayout dlMain;
    private LinearLayout llDrawer;

    private boolean crr;
    private boolean ysx;
    private boolean ygd;
    private boolean md;
    private boolean dt;
    private boolean hwg;
    private boolean jt;
    private Boolean[] wzType = {false, false, false, false, false, false, false};
    //屏幕参数
    PopupWindow mPopupWindow;
    //微信分享
    private IWXAPI mWeixinAPI;
    private String WEIXIN_APP_ID = "wx03d51cfead473cd9";

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_drawer);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);

        PublicApplication.isLoginCallback = false;
        startService(new Intent(this, ComityService.class));

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        initwindow();
        initMapView(savedInstanceState);
        initAMapLocation();
        initViewToTop();
        initViewToBottom();
        initViewSetting();
    }

    private void initwindow() {
        WindowManager windowManager = getWindowManager();
        width = windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 初始化设置页面
     */
    private void initViewSetting() {
        cbCrr = findViewById(R.id.cb_crr);
        cbYsx = findViewById(R.id.cb_ysx);
        cbHwg = findViewById(R.id.cb_hwg);
        cbDt = findViewById(R.id.cb_dt);
        cbMd = findViewById(R.id.cb_md);
        cbYgd = findViewById(R.id.cb_ygd);
        cbJt = findViewById(R.id.cb_jt);
        tvSubmit = findViewById(R.id.tv_submit);
        dlMain = findViewById(R.id.dl_main);
        llDrawer = findViewById(R.id.ll_drawer);

        cbCrr.setOnCheckedChangeListener(this);
        cbYsx.setOnCheckedChangeListener(this);
        cbHwg.setOnCheckedChangeListener(this);
        cbDt.setOnCheckedChangeListener(this);
        cbMd.setOnCheckedChangeListener(this);
        cbYgd.setOnCheckedChangeListener(this);
        cbJt.setOnCheckedChangeListener(this);

        tvSubmit.setOnClickListener(this);
        dlMain.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            //滑动时
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            //打开
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                initDrawerData();
            }

            //关闭
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            //改变时
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        });
    }

    /**
     * 初始化抽屉数据
     */
    private void initDrawerData() {
        crr = PublicApplication.settingInfo.getBoolean("crr", false);
        ysx = PublicApplication.settingInfo.getBoolean("ysx", false);
        ygd = PublicApplication.settingInfo.getBoolean("ygd", false);
        md = PublicApplication.settingInfo.getBoolean("md", false);
        dt = PublicApplication.settingInfo.getBoolean("dt", false);
        hwg = PublicApplication.settingInfo.getBoolean("hwg", false);
        jt = PublicApplication.settingInfo.getBoolean("jt", false);
        cbCrr.setChecked(crr);
        cbYsx.setChecked(ysx);
        cbHwg.setChecked(hwg);
        cbDt.setChecked(dt);
        cbMd.setChecked(md);
        cbYgd.setChecked(ygd);
        cbJt.setChecked(jt);
    }

    /**
     * 初始化上半部分view
     */
    private void initViewToTop() {
        rlTop = findViewById(R.id.rl_top);
        ivLeiDa = findViewById(R.id.iv_leida);
        lvVoice = findViewById(R.id.lv_voice);
        tvGpsRuo = findViewById(R.id.tv_ruo);

        rlTop.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                width * 7 / 15
        ));
        ivLeiDa.setLayoutParams(new LinearLayout.LayoutParams(
                width * 7 / 30, width * 7 / 30
        ));
        Glide.with(this)
                .load(R.drawable.img_lei_da)
                .asGif()
                .into(ivLeiDa);
        PublicApplication.mAdapter.setContext(this);
        lvVoice.setAdapter(PublicApplication.mAdapter);
    }

    /**
     * 初始化下半部分view
     */
    private void initViewToBottom() {
        rlBottom = findViewById(R.id.rl_bottom);
        tvSetting = findViewById(R.id.tv_setting);
        ivBiaoZhen = findViewById(R.id.iv_biao_zhen);
        ibTopLine = findViewById(R.id.ib_top_line);
        ibShowTicket = findViewById(R.id.ib_show_ticket);
        tvMe = findViewById(R.id.tv_me);
        tvShare = findViewById(R.id.tv_share);

        ibTopLine.setOnClickListener(this);
        ibShowTicket.setOnClickListener(this);
        tvMe.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        rlBottom.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                width * 7 / 15
        ));
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dlMain.isDrawerOpen(llDrawer))
                    dlMain.openDrawer(llDrawer);
            }
        });
        bit = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_biao_zhen);//读取资源里德图片
        bzWidth = bit.getWidth();
        bzHeight = bit.getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        if (mMapView != null) {
            mMapView.onResume();
        }
        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        }
        if (mLocationClient != null && mLocationOption != null && lbsTraceClient != null) {
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
            mLocationClient.startLocation();//启动定位
            lbsTraceClient.startTrace(traceListener); //开始采集,需要传入一个状态回调监听。
        }
        /**
         * 在地图上添加锚点
         */
        Thread thread = new Thread() {
            @Override
            public void run() {
                List<CoordinateBean> dataList = MapUtlis.getCoordinate100();
                for (int i = 0; i < dataList.size(); i++) {
                    double lat = Double.parseDouble(dataList.get(i).getLat());
                    double lon = Double.parseDouble(dataList.get(i).getLon());
                    LatLng latLng = new LatLng(lat, lon);
                    addMarkersToMap(latLng);
                }
            }
        };
        thread.start();
//        initXunFei();
        initService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        destroyService();
        if (mMapView != null)
            mMapView.onDestroy();
        if (mLocationClient != null)
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        if (lbsTraceClient != null)
            lbsTraceClient.stopTrace();//在不需要轨迹纠偏时（如行程结束），可调用此接口结束纠偏
        if (mCompositeDisposable != null)
            mCompositeDisposable.clear();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        if (mMapView != null)
            mMapView.onPause();
       /* if (mLocationClient != null)
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        if (lbsTraceClient != null)
            lbsTraceClient.stopTrace();//在不需要轨迹纠偏时（如行程结束），可调用此接口结束纠偏*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

    //*****************************************************高德地图部分

    /**
     * 高德地图
     *
     * @param savedInstanceState
     */
    private void initMapView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        setupLocationStyle();
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMaxZoomLevel(17);
    }


    /**
     * 高德定位
     */
    private void initAMapLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(8000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        lbsTraceClient = LBSTraceClient.getInstance(this);
    }

    /**
     * 在地图上添加marker
     */
    private MarkerOptions markerOption;
    Marker marker;

    private void addMarkersToMap(final LatLng latLng) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.img_jk))
                        .position(latLng)
                        .draggable(true);
                marker = aMap.addMarker(markerOption);
            }
        });
    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 轨迹纠偏回调
     */
    TraceStatusListener traceListener = new TraceStatusListener() {
        @Override
        public void onTraceStatus(List<TraceLocation> list, List<LatLng> list1, String s) {
            if (list != null)
                if (list.size() > 0)
                    mJPJiao = list.get(list.size() - 1).getBearing();
        }
    };

    /**
     * 高德定位回调
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                    /*Log.e(TAG, "onLocationChanged: "
                            + "--方向：" + aMapLocation.getBearing()
                            + "--精度：" + aMapLocation.getGpsAccuracyStatus()
                            + "--速度：" + aMapLocation.getSpeed()
                            + "--卫星数量：" + aMapLocation.getSatellites()
                            + "--卫星信号强度：" + (aMapLocation.getGpsAccuracyStatus() == 1 ? "强" : "弱")
                            + "--位置质量：" + aMapLocation.getLocationQualityReport());*/
                   /* LatLng latlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(
                                            latlng, 19, 0, 0)), null);*/
                if (aMapLocation.getGpsAccuracyStatus() == 1) {
                    tvGpsRuo.setVisibility(View.GONE);
                } else {
                    tvGpsRuo.setVisibility(View.VISIBLE);
                }

                getYDJiao(aMapLocation);
                //计算移动变量
                setmVariable(aMapLocation);

                if (comityBinder != null) {
                    comityBinder.startComity(aMapLocation.getLatitude() + "",
                            aMapLocation.getLongitude() + "", isFirst, mYDJiao);
                    comityBinder.startNoStop(aMapLocation.getLatitude() + "",
                            aMapLocation.getLongitude() + "", aMap);
                }
                PublicApplication.mVoiceManage.playVoice();
                isFirst = false;
                loginInspect();
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                if (aMapLocation.getErrorCode() == 12) {
                    Toast.makeText(MainActivity.this, "请在设备的设置中开启app的定位权限", Toast.LENGTH_SHORT).show();
//                        showToast("请在设备的设置中开启app的定位权限。");
                }
            }
        }
    }
    //*****************************************************高德地图部分

    //*****************************************************service部分
    private ComityService.MyBinder comityBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            switch (name.getClassName()) {
                case ComityService.TAG:
                    comityBinder = (ComityService.MyBinder) service;
                    break;
            }
        }
    };

    /**
     * 初始化服务
     */
    private void initService() {
        Intent bindIntent = new Intent(this, ComityService.class);//提示服务
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 销毁service
     */
    private void destroyService() {
        if (connection != null)
            unbindService(connection);
        stopService(new Intent(this, ComityService.class));
    }
    //*****************************************************service部分

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            destroyService();
            ScreenManager.getScreenManager().popAllActivityExceptOne(WelcomeActivity.class);
            ScreenManager.getScreenManager().popActivity();
            System.exit(0);
        }
    }

    /**
     * 计算运动角
     *
     * @param location
     */
    public void getYDJiao(AMapLocation location) {
        String newJiao = "0";

        if (mJPJiao != 0)
            newJiao = mJPJiao + "";
        else if (location.getBearing() != 0)
            newJiao = location.getBearing() + "";
        else
            newJiao = getZDYJiao(location) + "";

//        showToast("轨迹=" + mJPJiao + "--官方=" + location.getBearing() + "--计算=" + getZDYJiao(location));
        double newJiaoD = Double.parseDouble(newJiao);
        /**
         * 新角一次性偏移大于90度不进行计算角度的赋值 但记录下来 取第三次角
         */
        double big = 0;
        double smal = 0;
        if (mYDJiao > newJiaoD) {
            big = mYDJiao;
            smal = newJiaoD;
        } else {
            big = newJiaoD;
            smal = mYDJiao;
        }
        if (newJiaoD != 0)
            if (isCaiYong) {
                if ((big - smal) > 270 || (big - smal) < 90) {
                    lata = location.getLatitude() + "";
                    lona = location.getLongitude() + "";
                    mYDJiao = newJiaoD;
                    isCaiYong = true;
                } else {
                    isCaiYong = false;
                }
            } else {
                lata = location.getLatitude() + "";
                lona = location.getLongitude() + "";
                mYDJiao = newJiaoD;
                isCaiYong = true;
            }
        setLuoPan(Float.parseFloat(mYDJiao + ""));
    }

    /**
     * 设置罗盘角度
     */
    private void setLuoPan(float jiao) {
        Matrix m = new Matrix();
        m.setRotate(jiao);

        //做好旋转与大小之后，重新创建位图，0-width宽度上显示的区域，高度类似
        Bitmap b = Bitmap.createBitmap(bit, 0, 0, bzWidth, bzHeight, m, true);

        //显示图片
        ivBiaoZhen.setImageBitmap(b);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_submit:
                if (dlMain.isDrawerOpen(llDrawer))
                    dlMain.closeDrawer(llDrawer);
                PublicApplication.settingInfo.edit().putBoolean("crr", crr).apply();
                PublicApplication.settingInfo.edit().putBoolean("ysx", ysx).apply();
                PublicApplication.settingInfo.edit().putBoolean("ygd", ygd).apply();
                PublicApplication.settingInfo.edit().putBoolean("md", md).apply();
                PublicApplication.settingInfo.edit().putBoolean("dt", dt).apply();
                PublicApplication.settingInfo.edit().putBoolean("hwg", hwg).apply();
                PublicApplication.settingInfo.edit().putBoolean("jt", jt).apply();
                settingWZType();
                break;
            case R.id.ib_top_line:
                intent.setClass(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URLS, "http://222.223.239.214:802/?cat=3");
                startActivity(intent);
                break;
            case R.id.ib_show_ticket:
                intent.setClass(this, ShowTicketActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_me:
                intent.setClass(this, MeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_share:
                initPopuWindow();
                break;
            case R.id.tv_WX:
                shareWX();
                mPopupWindow.dismiss();
                break;
            case R.id.tv_FC:
                shareFC();
                mPopupWindow.dismiss();
                break;
        }
    }

    /**
     * 设置违章播报类型
     */
    private void settingWZType() {
        String wzTypeStr = "";
        for (int i = 0; i < wzType.length; i++) {
            if (!wzType[i])
                wzTypeStr = wzTypeStr + "," + (i + 1);
        }
        final String finalWzTypeStr = wzTypeStr;
        new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&userid=" + PublicApplication.loginInfo.getString("id", "")
                        + "&setviolation=" + finalWzTypeStr;
                String result = httpTool.httpPost(
                        PublicApplication.urlData.setup,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.setup)
                        .add("setviolation", finalWzTypeStr)
                        .add("userid", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("设置违章播报类型==..result.=", result + "");
            }
        }.start();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_crr:
                crr = b;
                wzType[0] = b;
                break;
            case R.id.cb_ysx:
                ysx = b;
                wzType[1] = b;
                break;
            case R.id.cb_hwg:
                hwg = b;
                wzType[2] = b;
                break;
            case R.id.cb_dt:
                dt = b;
                wzType[3] = b;
                break;
            case R.id.cb_md:
                md = b;
                wzType[4] = b;
                break;
            case R.id.cb_ygd:
                ygd = b;
                wzType[5] = b;
                break;
            case R.id.cb_jt:
                jt = b;
                wzType[6] = b;
                break;

        }
    }

    //popuWindow***************************************
    public void initPopuWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.item_share_pop, null);
        mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);

        //设置各个控件的点击响应
        contentView.findViewById(R.id.tv_WX).setOnClickListener(this);
        contentView.findViewById(R.id.tv_FC).setOnClickListener(this);

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        //显示PopupWindow
        View rootview = findViewById(R.id.fl_main);
//        mPopupWindow.showAsDropDown(rootview);
        mPopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }
    //popuWindow***************************************

    /**
     * 分享到朋友圈
     */
    private void shareFC() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://mp.weixin.qq.com/s/n-HzeLQU0gFv351b6tcOQA";

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "灵狗";
        msg.description = PublicApplication.WXShare;
        Bitmap thumb = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.img_h_logo);
        msg.thumbData = Utlis.Bitmap2Bytes(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = build
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;

        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, false);
        }

        if (!mWeixinAPI.isWXAppInstalled()) {
            showToast("没有安装微信");
            return;
        }

        mWeixinAPI.registerApp(WEIXIN_APP_ID);
        if (mWeixinAPI != null)
            mWeixinAPI.sendReq(req);
    }

    /**
     * 分享到微信
     */
    private void shareWX() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://mp.weixin.qq.com/s/n-HzeLQU0gFv351b6tcOQA";

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "灵狗";
        msg.description = PublicApplication.WXShare;
        Bitmap thumb = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.img_h_logo);
        msg.thumbData = Utlis.Bitmap2Bytes(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = build
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, false);
        }

        if (!mWeixinAPI.isWXAppInstalled()) {
            showToast("没有安装微信");
            return;
        }

        mWeixinAPI.registerApp(WEIXIN_APP_ID);
        if (mWeixinAPI != null)
            mWeixinAPI.sendReq(req);
    }

    /**
     * 计算移动变量
     *
     * @param location
     */
    private void setmVariable(final AMapLocation location) {
        new Thread() {
            @Override
            public void run() {
                if (location.getSpeed() > 5f) {
                    PublicApplication.mVariableSpeed = location.getSpeed();
                }
            }
        }.start();
    }

    /**
     * 自定义方法计算运动角度
     *
     * @param location
     * @return
     */
    public double getZDYJiao(AMapLocation location) {
        double zdyJiao = 0;
        if (!lata.equals("0")) {
            zdyJiao = MapUtlis.getDirection(lata, lona, location.getLatitude() + "", location.getLongitude() + "");
            double big;
            double smal;
            if (mYDJiao > zdyJiao) {
                big = mYDJiao;
                smal = zdyJiao;
            } else {
                smal = mYDJiao;
                big = zdyJiao;
            }
            if (big - smal >= 15) {
                if (big > 337 && smal < 22)
                    if ((big - smal) > 180) {
                        zdyJiao = big + (big - smal) / 2;
                    } else {
                        zdyJiao = smal + (big - smal) / 2;
                    }
            } else
                zdyJiao = mYDJiao;
        }
        return zdyJiao;
    }

    private void loginInspect() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.tokendouble)
                        .add("userid", PublicApplication.loginInfo.getString("id", ""))
                        .add("token", PublicApplication.loginInfo.getString("token", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("验证登录==..result.=", result + "");
                try {
                    if (result != null)
                        if (!TextUtils.isEmpty(result)) {
                            Gson gson = new Gson();
                            BaseBean dataBean = new BaseBean();
                            dataBean = gson.fromJson(result,
                                    BaseBean.class);
                            int state = dataBean.getState();
                            emitter.onNext(state);
                        }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (mCompositeDisposable != null)
                    mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                if (integer == 99) {
                    PublicApplication.loginInfo.edit().putString("user_login",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("id",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("user_nicename",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("user_email",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("display_name",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("phone",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("token",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("user_url",
                            "").apply();
                    PublicApplication.loginInfo.edit().putString("avatar",
                            "").apply();

                    Intent stopIntent = new Intent();
                    stopIntent.setAction("com.soowin.cleverdog.service.ComityService");//你定义的service的action
                    stopIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                    stopService(stopIntent);

                    PublicApplication.mVoiceManage.delVoiceAll();

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    ScreenManager.getScreenManager().popAllActivityExceptOne(WelcomeActivity.class);
                    finish();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        };
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
/*AMap.OnMyLocationChangeListener myLocationlistener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(final Location location) {
            if (!HttpTool.isOpenNetwork(MainActivity.this)) {
                startActivity(new Intent(MainActivity.this, HttpErrorActivity.class));
                finish();
            } else {
                if (location.getLatitude() == 0 || location.getLongitude() == 0)
                    showToast("定位不准确，请手动打开位置信息");
                if (getGPSStrength(location)) {
                    tvGpsRuo.setVisibility(View.VISIBLE);
                } else {
                    tvGpsRuo.setVisibility(View.GONE);
                }

                getYDJiao(location);
                //计算移动变量
                setmVariable(location);

                if (comityBinder != null) {
                    comityBinder.startComity(location.getLatitude() + "",
                            location.getLongitude() + "", isFirst, mYDJiao);
                    comityBinder.startNoStop(location.getLatitude() + "",
                            location.getLongitude() + "", aMap);
                }
//                if (isOkSpeak)
                isSpeaking = PublicApplication.mVoiceManage.playVoice(
                        mTts, mSynListener100, mSynListener20, isSpeaking);
//                else
//                    initXunFei();
                isFirst = false;
                lata = location.getLatitude() + "";
                lona = location.getLongitude() + "";
            }
        }
    };*/
/**
 * 获取信号强度
 * 1、每两秒计算一次 两点间距离 如果 大于 66米 就显示信号弱
 * 2、计算五个次相加的距离 如果 大于
 *//*
    public boolean getGPSStrength(Location location) {
        boolean returnThis = false;
        float accuracy = location.getAccuracy();
        if (accuracy < 0)
            returnThis = true;
        if (accuracy >= PublicApplication.longOne)
            returnThis = true;

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double distance = MapUtlis.getDistance(lata, lona, lat + "", lon + "");
        if (distance > PublicApplication.mVariableSpeed )
            returnThis = true;

        distance1 = distance2;
        distance2 = distance3;
        distance3 = distance4;
        distance4 = distance5;
        distance5 = distance;
        if ((distance1 + distance2 + distance3 + distance4 + distance5) > 33 * 5)
            returnThis = true;

        return returnThis;
    }*/
 /*    double distance1 = 0;
    double distance2 = 0;
    double distance3 = 0;
    double distance4 = 0;
    double distance5 = 0;*/