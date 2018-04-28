package com.soowin.cleverdog.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.index.WebViewActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.login.IsOkBean;
import com.soowin.cleverdog.info.login.RegisterBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public static final String UNIONID = "unionid";
    public static final String OPENID = "openid";
    private String openid = "";
    private String unionid = "";

    private TextView tvLogin;
    private EditText etPhone;
    private EditText etCode;
    private EditText etPassWord;
    private TextView tvRegister;
    private TextView tvGetCode;
    private CheckBox cbVisibility;
    private boolean YANZHENG = true;//是否可以获取验证码
    private CountDownTimer cdtimer;

    private String mPhone;
    //微信分享
    private IWXAPI mWeixinAPI;
    private String WEIXIN_APP_ID = "wx03d51cfead473cd9";
    //屏幕参数
    PopupWindow mPopupWindow;

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (HttpTool.isHttpError(msg, null, getApplicationContext())) {
                        dismissDialog();
                        return;
                    } else if (HttpTool.isHttpNull(msg, null, getApplicationContext())) {
                        dismissDialog();
                        return;
                    } else if (msg.what == 1) {
                        dismissDialog();
                        Gson gson = new Gson();
                        IsOkBean dataBean = new IsOkBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                IsOkBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:

                                break;
                            default:
                                Toast.makeText(RegisterActivity.this, "该手机号已经被注册",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else if (msg.what == 2) {
                        dismissDialog();
                        Gson gson = new Gson();
                        RegisterBean dataBean = new RegisterBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                RegisterBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                showToast("注册成功，请登录");
                                finish();
                                break;
                            default:
                                Toast.makeText(RegisterActivity.this, "网络连接错误！请稍后重试",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                dismissDialog();
                Toast.makeText(RegisterActivity.this, "网络连接错误！请稍后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈

        openid = getIntent().getStringExtra(OPENID);
        unionid = getIntent().getStringExtra(UNIONID);

        initView();
    }

    private void initView() {
        tvLogin = findViewById(R.id.tv_login);
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        etPassWord = findViewById(R.id.et_password);
        tvRegister = findViewById(R.id.tv_register);
        tvGetCode = findViewById(R.id.tv_get_code);
        cbVisibility = findViewById(R.id.cb_visibility);
        findViewById(R.id.tv_kaitong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URLS, "http://mp.weixin.qq.com/s/n-HzeLQU0gFv351b6tcOQA");
                intent.putExtra(WebViewActivity.TITLE, "开通");
                startActivity(intent);
            }
        });

        tvLogin.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        cbVisibility.setOnCheckedChangeListener(cbListener);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                YANZHENG = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            case R.id.tv_login:
                finish();
                break;
            case R.id.tv_get_code:
                //获取验证码
                String phone = etPhone.getText().toString().trim();
                if (YANZHENG)
                    if (phone != null)
                        if (phone.length() == 11) {
                            new CountDownTimer(60000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    tvGetCode.setTextColor(getResources().getColor(R.color.gray));
                                    tvGetCode.setText(millisUntilFinished / 1000 + "S 后重试");
                                    YANZHENG = false;
                                }

                                public void onFinish() {
                                    tvGetCode.setText("获取验证码");
                                    YANZHENG = true;
                                }
                            }.start();
                            getCode(phone);
                        } else
                            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();

                break;
            case R.id.tv_register:
                register();
                break;
        }
    }

    //注册按钮点击事件
    private void register() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassWord.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        if (StrUtils.isEmpty(phone) | StrUtils.isEmpty(password) |
                StrUtils.isEmpty(code)) {
            showToast("请填写手机号、密码、验证码");
        } else {
            if (phone.length() == 11 && code.length() > 5 && code.length() < 17) {
                gotoRegister();
            } else {
                showToast("请输入正确的手机号、验证码");
            }
        }
    }

    //网络请求注册
    private void gotoRegister() {
        showDialog();
        final String pass = etPassWord.getText().toString().trim();
        final String code = etCode.getText().toString().trim();
        Thread thread = new Thread() {
            @Override
            public void run() {
                RequestBody body;
                if (StrUtils.isEmpty(openid) || StrUtils.isEmpty(unionid)) {
                    body = new FormBody.Builder()
                            .add("json", PublicApplication.urlData.registe)
                            .add("username", mPhone)
                            .add("password", Utlis.toBase64(pass))
                            .add("code", code)
                            .build();
                } else {
                    body = new FormBody.Builder()
                            .add("json", PublicApplication.urlData.registe)
                            .add("username", mPhone)
                            .add("password", Utlis.toBase64(pass))
                            .add("openid", openid)
                            .add("unionid", unionid)
                            .add("code", code)
                            .build();
                }

                String result = HttpTool.okPost(body);
                Log.e("注册用户==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        };
        thread.start();
    }

    //获取验证码
    public void getCode(final String phone) {
        mPhone = phone;
        showDialog();
        Thread thread = new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.getVerification)
                        .add("phone", phone)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("获取验证码==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        };
        thread.start();
        tvGetCode.setTextColor(getResources().getColor(R.color.gray));
        YANZHENG = false;
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
        webpage.webpageUrl = PublicApplication.urlData.hostUrl;

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
        webpage.webpageUrl = PublicApplication.urlData.hostUrl;

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
}
