package com.soowin.cleverdog.activity.index;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.login.LoginActivity;
import com.soowin.cleverdog.activity.welcome.HttpErrorActivity;
import com.soowin.cleverdog.activity.welcome.WelcomeActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.BaseBean;
import com.soowin.cleverdog.info.login.LoginBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.MapUtlis;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.SettingUtlis;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 录入违章点 1
 */
public class InputActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = InputActivity.class.getSimpleName();
    //title
    private TextView tvTitle;
    private TextView tvOk;
    //高德地图
    private MapView mMapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    double distance1 = 10;
    double distance2 = 10;
    double distance3 = 10;
    double distance4 = 10;
    double distance5 = 10;
    private String lata = "0";
    private String lona = "0";
    private boolean isStart = false;//是否开始采点
    //在地图上添加marker
    private MarkerOptions markerOption;
    Marker marker;
    //主要布局
    private RadioButton rbCrr;
    private RadioButton rbYsx;
    private RadioButton rbHwg;
    private RadioButton rbDt;
    private RadioButton rbMd;
    private RadioButton rbYgd;
    private RadioButton rbTs;
    private CheckBox cbStart;
    private TextView tvSubmit;
    private EditText etName;

    private int isType = 0;
    private String name = "";


    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissDialog();
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        dismissDialog();
                        Gson gson = new Gson();
                        BaseBean dataBean = new BaseBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                BaseBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                etName.setText("");
                                lata = "0";
                                lona = "0";
                                name = "";
                                showToast(dataBean.getMessage());
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                } else {
                    showToast("网络状态异常，请稍后重试");
                    dismissDialog();
                }
            } catch (Exception e) {
                showToast("网络状态异常，请稍后重试");
                Log.e("DingDanLBActivity", e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        initTitle();
        initMapView(savedInstanceState);
        initMainView();
    }

    private void initMainView() {
        rbCrr = findViewById(R.id.rb_crr);
        rbYsx = findViewById(R.id.rb_ysx);
        rbHwg = findViewById(R.id.rb_hwg);
        rbDt = findViewById(R.id.rb_dt);
        rbMd = findViewById(R.id.rb_md);
        rbYgd = findViewById(R.id.rb_ygd);
        rbTs = findViewById(R.id.rb_ts);
        cbStart = findViewById(R.id.cb_start);
        tvSubmit = findViewById(R.id.tv_submit);
        etName = findViewById(R.id.et_name);

        rbCrr.setOnCheckedChangeListener(changeListener);
        rbYsx.setOnCheckedChangeListener(changeListener);
        rbHwg.setOnCheckedChangeListener(changeListener);
        rbDt.setOnCheckedChangeListener(changeListener);
        rbMd.setOnCheckedChangeListener(changeListener);
        rbYgd.setOnCheckedChangeListener(changeListener);
        rbTs.setOnCheckedChangeListener(changeListener);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = etName.getText().toString().trim();
            }
        });
        cbStart.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                switch (compoundButton.getId()) {
                    case R.id.rb_crr:
                        isType = 1;
                        break;
                    case R.id.rb_ysx:
                        isType = 2;
                        break;
                    case R.id.rb_hwg:
                        isType = 3;
                        break;
                    case R.id.rb_dt:
                        isType = 4;
                        break;
                    case R.id.rb_md:
                        isType = 5;
                        break;
                    case R.id.rb_ygd:
                        isType = 6;
                        break;
                    case R.id.rb_ts:
                        isType = 8;
                        break;
                }

            }
        }
    };

    private String getType(int i) {
        //（cmpi.车让人违章
        //rslv.轧实线违章
        //ygv.黄色网格违章
        //sluv.直行道掉头违章
        //wv.鸣笛违章
        //hbv.远光灯违章
        //nstop,禁止停车）
        String type = "";
        switch (i) {
            case 1:
                type = "cmpi";
                break;
            case 2:
                type = "rslv";
                break;
            case 3:
                type = "ygv";
                break;
            case 4:
                type = "sluv";
                break;
            case 5:
                type = "wv";
                break;
            case 6:
                type = "hbv";
                break;
            case 8:
                type = "other";
                break;
        }
        return type;
    }

    private void initMapView(Bundle savedInstanceState) {
        LatLng centerSJZPoint = new LatLng(38.0483, 114.52127);
        // 定义了一个配置 AMap 对象的参数类
        AMapOptions mapOptions = new AMapOptions();
        // 设置了一个可视范围的初始化位置
        mapOptions.camera(new CameraPosition(centerSJZPoint, 19f, 0, 0));
        // 定义一个 MapView 对象，构造方法中传入 mapOptions 参数类
        mMapView = new MapView(this, mapOptions);
        // 调用 onCreate方法 对 MapView LayoutParams 设置
        mMapView = findViewById(R.id.mv_my_mv);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.transparent));
        myLocationStyle.strokeColor(getResources().getColor(R.color.transparent));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(myLocationlistener);
    }

    AMap.OnMyLocationChangeListener myLocationlistener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(final Location location) {
            if (!HttpTool.isOpenNetwork(InputActivity.this)) {
                startActivity(new Intent(InputActivity.this, HttpErrorActivity.class));
                finish();
            } else {
                if (isStart) {
                    if (location.getLatitude() == 0f || location.getLongitude() == 0f)
                        showToast("定位不准确，请手动打开位置信息");
                    addMarkersToMap(location);
                    lata = location.getLatitude() + "";
                    lona = location.getLongitude() + "";
                }
            }
        }
    };

    private void addMarkersToMap(final Location location) {
        aMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.img_addr))
                .position(latLng)
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        if (getGPSStrength(location)) {
            isStart = true;
        } else {
            showToast("采集成功");
            distance1 = 10;
            distance2 = 10;
            distance3 = 10;
            distance4 = 10;
            distance5 = 10;
            isStart = false;
            cbStart.setChecked(false);
        }
    }

    /**
     * 计算是否移动中
     * 1、每两秒计算一次 两点间距离 如果 大于 1米 认定为移动中
     * 2、计算五个次相加的距离 如果 大于 5米
     * 3、精度小与播报距离最小值
     */
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
        if (distance > 1)
            returnThis = true;

        distance1 = distance2;
        distance2 = distance3;
        distance3 = distance4;
        distance4 = distance5;
        distance5 = distance;
        if ((distance1 + distance2 + distance3 + distance4 + distance5) > 5)
            returnThis = true;

        return returnThis;
    }

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        tvOk = findViewById(R.id.tv_ok);


        tvTitle.setText("坐标录入");
        tvOk.setVisibility(View.VISIBLE);
        tvOk.setOnClickListener(this);
        tvOk.setText("退出登录");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_start:
                isStart = cbStart.isChecked();
                showToast("定位开始，请等待十秒");
                break;
            case R.id.tv_submit:
                if (isStart)
                    showToast("正在定位请稍后");
                else if (isType == 0)
                    showToast("请选择违章类型");
                else if (lata.equals("0") || lona.equals("0"))
                    showToast("定位信息采集失败，请重试");
                else
                    startSubmit();
                break;
            case R.id.tv_ok:
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
                startActivity(new Intent(InputActivity.this, LoginActivity.class));
                ScreenManager.getScreenManager().popAllActivityExceptOne(WelcomeActivity.class);
                finish();
                break;
        }
    }

    /**
     * 上传定位信息
     */
    private void startSubmit() {
        showDialog();
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.addregion)
                        .add("section", name)
                        .add("signs", getType(isType))
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .add("position", lona + "," + lata)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("上传定位信息==..result.=", result);
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        if (mMapView != null) {
            mMapView.onResume();
        }
        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        if (mMapView != null)
            mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

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
            ScreenManager.getScreenManager().popAllActivityExceptOne(WelcomeActivity.class);
            ScreenManager.getScreenManager().popActivity();
            System.exit(0);
        }
    }
}
