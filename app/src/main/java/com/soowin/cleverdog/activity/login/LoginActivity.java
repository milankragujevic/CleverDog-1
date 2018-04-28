package com.soowin.cleverdog.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.index.InputActivity;
import com.soowin.cleverdog.activity.index.MainActivity;
import com.soowin.cleverdog.activity.welcome.WelcomeActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.login.LoginBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.SettingUtlis;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private TextView tvRegister;
    private EditText etPhone;
    private EditText etPassWord;
    private CheckBox cbVisibility;
    private TextView tvLogin;
    private TextView tvGetPassWord;

    private ImageView ivWeiXin;

    private IWXAPI mWeixinAPI;
    private String WEIXIN_APP_ID = "wx03d51cfead473cd9";

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        dismissDialog();
                        Gson gson = new Gson();
                        LoginBean dataBean = new LoginBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                LoginBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                /**
                                 * 本地数据库
                                 */
                                PublicApplication.loginInfo.edit().putString("id",
                                        dataBean.getResult().getID()).apply();
                                PublicApplication.loginInfo.edit().putString("user_login",
                                        dataBean.getResult().getUser_login()).apply();
                                PublicApplication.loginInfo.edit().putString("token",
                                        dataBean.getResult().getToken()).apply();
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


                                showToast("登录成功");
                                Intent intent2;
                                if (dataBean.getResult().getUser_status().equals("1"))
                                    intent2 = new Intent(LoginActivity.this, InputActivity.class);
                                else {
                                    int myV = PublicApplication.db.getVersion();
                                    int webV = Integer.parseInt(PublicApplication.loginInfo.getString("versionnumber", ""));
                                    if (myV != webV) {
                                        Intent mIntent = new Intent();
                                        mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
                                        mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                                        startService(mIntent);
                                    }
                                    intent2 = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                startActivity(intent2);
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
                Log.e(TAG, e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈

        PublicApplication.isLoginCallback = true;

        initView();
        initDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PublicApplication.WXLogin == 1) {
            PublicApplication.WXLogin = 0;
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (PublicApplication.WXLogin == 3) {
            PublicApplication.WXLogin = 0;
            Intent intent = new Intent(LoginActivity.this, InputActivity.class);
            startActivity(intent);
            finish();
        } else if (PublicApplication.WXLogin == 2) {
            PublicApplication.WXLogin = 0;
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra(RegisterActivity.OPENID, PublicApplication.WXopenid);
            intent.putExtra(RegisterActivity.UNIONID, PublicApplication.WXunionid);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (data != null && requestCode == 0) {
            int status = data.getIntExtra("status", 0);//0注册成功 返回直接页面 非0返回登录页面
            if (status == 0)
                finish();
        }*/
    }

    private void initView() {
        tvRegister = findViewById(R.id.tv_register);
        etPhone = findViewById(R.id.et_phone);
        etPassWord = findViewById(R.id.et_password);
        cbVisibility = findViewById(R.id.cb_visibility);
        tvLogin = findViewById(R.id.tv_login);
        tvGetPassWord = findViewById(R.id.tv_get_password);
        ivWeiXin = findViewById(R.id.iv_weixin);

        tvGetPassWord.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        cbVisibility.setOnCheckedChangeListener(cbListener);
        ivWeiXin.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener cbListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(
                        CompoundButton compoundButton, boolean b) {
                    if (b)
                        etPassWord.setInputType(InputType.TYPE_CLASS_TEXT);
                    else
                        etPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_get_password:
                Intent intent = new Intent(this, GetPassWordActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_register:
                Intent intent1 = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent1, 0);
                break;
            case R.id.tv_login:
                login();
                break;
            case R.id.iv_weixin:
                PublicApplication.WXLogin = 0;
                PublicApplication.WXopenid = "";
                PublicApplication.WXunionid = "";
                loginWithWeixin();
                break;
        }
    }

    /**
     * 微信登陆
     */
    private void loginWithWeixin() {
        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, false);
        }

        if (!mWeixinAPI.isWXAppInstalled()) {
            showToast("没有安装微信");
            return;
        }

        mWeixinAPI.registerApp(WEIXIN_APP_ID);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWeixinAPI.sendReq(req);
    }

    //登录按钮点击事件
    private void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassWord.getText().toString().trim();
        if (StrUtils.isEmpty(phone) | StrUtils.isEmpty(password)) {
            showToast("请填写手机号、密码");
        } else {
            showDialog();
            gotoLogin(phone, password);
        }
    }

    //网络请求登录
    private void gotoLogin(final String phone, final String password) {
        new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&username=" + phone
                        + "&password=" + Utlis.toBase64(password);
                String result = httpTool.httpPost(
                        PublicApplication.urlData.login,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.login)
                        .add("username", phone)
                        .add("password", Utlis.toBase64(password))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("登录==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public void initDialog() {
        super.initDialog();
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
