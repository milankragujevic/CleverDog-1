package com.soowin.cleverdog.activity.welcome;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.index.InputActivity;
import com.soowin.cleverdog.activity.index.MainActivity;
import com.soowin.cleverdog.activity.login.LoginActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.login.LoginBean;
import com.soowin.cleverdog.info.login.ToolListBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.SettingUtlis;
import com.soowin.cleverdog.utlis.permissions.PermissionsActivity;
import com.soowin.cleverdog.utlis.permissions.PermissionsChecker;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 欢迎页
 */
public class WelcomeActivity extends BaseActivity {
    public static final String TAG = WelcomeActivity.class.getSimpleName();

    public static final int LOGINCODE = 1001;

    private ImageView ivGG;

    /**
     * 权限部分
     */
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
//            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (HttpTool.isHttpError(msg, null, WelcomeActivity.this)) {
                    gotoLogin();
                    return;
                } else if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (HttpTool.isHttpError(msg, null, getApplicationContext())) {
                        loginError();
                        return;
                    } else if (HttpTool.isHttpNull(msg, null, getApplicationContext())) {
                        loginError();
                        return;
                    } else if (msg.what == 1) {
                        Gson gson = new Gson();
                        final LoginBean dataBean = gson.fromJson(msg.obj.toString(),
                                LoginBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                PublicApplication.loginInfo.edit().putString("id",
                                        dataBean.getResult().getID()).apply();
                                PublicApplication.loginInfo.edit().putString("user_login",
                                        dataBean.getResult().getUser_login()).apply();
                                PublicApplication.loginInfo.edit().putString("user_nicename",
                                        dataBean.getResult().getUser_nicename()).apply();
                                PublicApplication.loginInfo.edit().putString("user_email",
                                        dataBean.getResult().getUser_email()).apply();
                                PublicApplication.loginInfo.edit().putString("user_url",
                                        dataBean.getResult().getUser_url()).apply();
                                PublicApplication.loginInfo.edit().putString("display_name",
                                        dataBean.getResult().getDisplay_name()).apply();
                                PublicApplication.loginInfo.edit().putString("phone",
                                        dataBean.getResult().getPhone()).apply();
                                PublicApplication.loginInfo.edit().putString("versionnumber",
                                        dataBean.getResult().getVersionnumber()).apply();
                                PublicApplication.loginInfo.edit().putString("avatar",
                                        dataBean.getResult().getAvatar()).apply();

                                SettingUtlis.setWZType(dataBean.getResult().getSetviolation());


                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent2;
                                        if (dataBean.getResult().getUser_status().equals("1"))
                                            intent2 = new Intent(WelcomeActivity.this, InputActivity.class);
                                        else {
                                            int myV = PublicApplication.db.getVersion();
                                            int webV = Integer.parseInt(PublicApplication.loginInfo.getString("versionnumber", ""));
                                            if (myV != webV) {
                                                Intent mIntent = new Intent();
                                                mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
                                                mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                                                startService(mIntent);
                                            }
                                            intent2 = new Intent(WelcomeActivity.this, MainActivity.class);
                                        }
                                        startActivity(intent2);
                                    }
                                }, 2000);
                                break;
                            default:
                                loginError();
                                break;
                        }
                    } else if (msg.what == 2) {
                        Gson gson = new Gson();
                        final ToolListBean dataBean = gson.fromJson(msg.obj.toString(),
                                ToolListBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                Glide.with(WelcomeActivity.this)
                                        .load(dataBean.getResult())
                                        .error(R.drawable.img_gg)
                                        .into(ivGG);
                                break;
                            default:
                                loginError();
                                break;
                        }
                    }
                } else {
                    gotoLogin();
                }
            } catch (Exception e) {
                loginError();
                Toast.makeText(WelcomeActivity.this, "网络连接错误！请稍后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        mPermissionsChecker = new PermissionsChecker(this);// 权限检测器

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {// 权限检测器
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        } else {
            addTool();
            verificationLogin();
            initDataBase();
            initBaiDu();
        }
    }

    /**
     * 添加广告
     */
    private void addTool() {
        ivGG = findViewById(R.id.iv_gg);
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.ToolList)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("广告==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 初始化数据库
     */
    private void initDataBase() {
        PublicApplication.db.onCreatAllTable();
    }

    //*****************************************************百度推送部分
    //初始化百度推送
    private void initBaiDu() {
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY, "V3rBGfBqCwjjwECsdGCaERNR");
//                PushConstants.LOGIN_TYPE_API_KEY, "VT3oGZh3tcGp2UAW4AxYy5Zo");
    }//*****************************************************百度推送部分


    private void gotoLogin() {
        if (!HttpTool.isOpenNetwork(this)) {
            startActivity(new Intent(this, HttpErrorActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGINCODE)
            if (data != null)
                if (data.getBooleanExtra("isOut", true))
                    finish();
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {// 权限检测器
            showToast("缺少主要权限无法访问，请谅解");
        } else {
            addTool();
            verificationLogin();
            initDataBase();
            initBaiDu();
        }
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

    /**
     * 验证登录
     */
    private void verificationLogin() {
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.autologin)
                        .add("token", PublicApplication.loginInfo.getString("token", ""))
                        .add("userid", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("验证登录==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    private void loginError() {
        PublicApplication.loginInfo.edit().putString("id",
                "").apply();
        PublicApplication.loginInfo.edit().putString("user_login",
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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gotoLogin();
            }
        }, 2000);
    }
}
