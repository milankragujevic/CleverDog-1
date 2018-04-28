package com.soowin.cleverdog.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.login.IsOkBean;
import com.soowin.cleverdog.info.login.IsOkBean2;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 忘记密码
 */
public class GetPassWordActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvGetPassword;
    private EditText etPhone;
    private EditText etCode;
    private EditText etPassWord;
    private TextView tvRegister;
    private TextView tvGetCode;
    private CheckBox cbVisibility;
    private boolean YANZHENG = true;//是否可以获取验证码
    int i;
    private String mPhone;

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        Gson gson = new Gson();
                        IsOkBean dataBean = new IsOkBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                IsOkBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                Log.e("queRenA", "数据请求成功");
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    } else if (msg.what == 2) {
                        Gson gson = new Gson();
                        IsOkBean2 dataBean = new IsOkBean2();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                IsOkBean2.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                Log.e("queRenA", "数据请求成功");
                                getPassword();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    } else if (msg.what == 3) {
                        Gson gson = new Gson();
                        IsOkBean2 dataBean = new IsOkBean2();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                IsOkBean2.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                Log.e("queRenA", "数据请求成功");
                                showToast(dataBean.getMessage());
                                finish();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(GetPassWordActivity.this, "网络连接错误！请稍后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        i = getIntent().getIntExtra("setting", -1);

        initTitle();
        initView();
    }

    private void initTitle() {
        ivClose = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        ivClose.setVisibility(View.VISIBLE);
        tvTitle.setText("忘记密码");
    }

    private void initView() {
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        etPassWord = findViewById(R.id.et_password);
        tvRegister = findViewById(R.id.tv_register);
        tvGetCode = findViewById(R.id.tv_get_code);
        cbVisibility = findViewById(R.id.cb_visibility);
        tvGetPassword = findViewById(R.id.tv_get_password);

        tvGetPassword.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        cbVisibility.setOnCheckedChangeListener(cbListener);
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
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_get_code:
                String phone = etPhone.getText().toString().trim();
                if (YANZHENG) {
                    if (phone != null)
                        if (phone.length() == 11) {
                            new CountDownTimer(60000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    tvGetCode.setText(millisUntilFinished / 1000 + "S 后重试");
                                    YANZHENG = false;
                                }

                                public void onFinish() {
                                    tvGetCode.setText("获取验证码");
                                    YANZHENG = true;
                                }
                            }.start();
                            getYz(phone);
                        } else
                            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_get_password:
                String password = etPassWord.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                if (StrUtils.isEmpty(mPhone) | StrUtils.isEmpty(password) |
                        StrUtils.isEmpty(code)) {
                    showToast("请填写手机号、密码、验证码");
                } else {
                    if (mPhone.length() == 11 && code.length() > 5 && code.length() < 17) {
                        yzCode(code);
                    } else {
                        showToast("请输入正确的手机号、密码");
                    }
                }
                break;
        }
    }

    //注册按钮点击事件
    private void yzCode(final String code) {
        Thread thread = new Thread() {
            @Override
            public void run() {
               /* HttpTool httpTool = new HttpTool();
                String data = "&phone=" + mPhone +
                        "&code=" + code;
                String result = httpTool.httpPost(
                        PublicApplication.urlData.verificationCode,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.verificationCode)
                        .add("phone", mPhone)
                        .add("code", code)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("验证验证码==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        };
        thread.start();
    }

    //重置密码
    private void getPassword() {
        final String new_pwd = etPassWord.getText().toString().trim();
        Thread thread = new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&phone=" + mPhone +
                        "&new_pwd=" + new_pwd;
                String result = httpTool.httpPost(
                        PublicApplication.urlData.resetPassword,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.resetPassword)
                        .add("phone", mPhone)
                        .add("new_pwd", new_pwd)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("重置密码==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 3;
                handle.sendMessage(msg);
            }
        };
        thread.start();
    }

    //获取验证码
    public void getYz(final String phone) {
        mPhone = phone;
        Thread thread = new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&phone=" + phone;
                String result = httpTool.httpPost(
                        PublicApplication.urlData.sendVerification,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.sendVerification)
                        .add("phone", phone)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("获取忘记密码验证码==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        };
        thread.start();

    }
}
