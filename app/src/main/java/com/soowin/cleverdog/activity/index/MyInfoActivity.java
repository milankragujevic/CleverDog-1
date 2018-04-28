package com.soowin.cleverdog.activity.index;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.BaseBean;
import com.soowin.cleverdog.info.index.UploadImgBean;
import com.soowin.cleverdog.info.login.GetPersonalDataBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;
import com.soowin.cleverdog.utlis.permissions.PermissionsActivity;
import com.soowin.cleverdog.utlis.permissions.PermissionsChecker;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 个人信息
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MyInfoActivity.class.getSimpleName();

    private int sex = 0;//0 男  1 女

    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tvOk;

    private EditText etNice;
    private EditText etName;
    private EditText etPhone;
    private EditText etPlate;
    private RadioButton rbNan;
    private RadioButton rbNv;

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
                        GetPersonalDataBean dataBean = new GetPersonalDataBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                GetPersonalDataBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                etNice.setText(dataBean.getResult().getUser_nicename());
                                etName.setText(dataBean.getResult().getDisplay_name());
                                etPhone.setText(dataBean.getResult().getPhone());
                                etPlate.setText(dataBean.getResult().getPlate());
                                if (dataBean.getResult().getSex() == 0) {
                                    rbNan.setChecked(true);
                                } else {
                                    rbNv.setChecked(true);
                                }
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    } else if (msg.what == 3) {
                        dismissDialog();
                        Gson gson = new Gson();
                        BaseBean dataBean = new BaseBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                BaseBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                showToast("修改成功");
                                finish();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("DingDanLBActivity", e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈

        initTitle();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 查询个人资料
     */
    private void initData() {
        showDialog();
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.getPersonalData)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("查询个人资料==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        etNice = findViewById(R.id.et_nice);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etPlate = findViewById(R.id.et_plate);
        rbNan = findViewById(R.id.rb_nan);
        rbNv = findViewById(R.id.rb_nv);

        rbNan.setOnCheckedChangeListener(checkedListener);
        rbNv.setOnCheckedChangeListener(checkedListener);
    }

    CompoundButton.OnCheckedChangeListener checkedListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        switch (compoundButton.getId()) {
                            case R.id.rb_nan:
                                sex = 0;
                                break;
                            case R.id.rb_nv:
                                sex = 1;
                                break;
                        }
                }
            };

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        tvOk = findViewById(R.id.tv_ok);

        tvTitle.setText("个人信息");
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        tvOk.setVisibility(View.VISIBLE);
        tvOk.setText("完成");
        tvOk.setTextColor(Color.WHITE);
        tvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_ok:
                submit();
                break;
        }
    }

    /**
     * 提交修改
     */
    private void submit() {
        String Nice = etNice.getText().toString().trim();
        String Name = etName.getText().toString().trim();
        String Phone = etPhone.getText().toString().trim();
        String Plate = etPlate.getText().toString().trim();
        if (StrUtils.isEmpty(Nice) || StrUtils.isEmpty(Name) ||
                StrUtils.isEmpty(Phone) || StrUtils.isEmpty(Plate)) {
            showToast("您还有未填写项");
        } else {
            updataMyInfo(Nice, Name, Phone, Plate);
        }
    }

    /**
     * 上传个人信息
     *
     * @param phone
     * @param name
     * @param nice
     */
    private void updataMyInfo(final String nice, final String name,
                              final String phone, final String plate) {
        showDialog();
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.editPersonalData)
                        .add("user_nicename", nice)
                        .add("display_name", name)
                        .add("phone", phone)
                        .add("plate", plate)
                        .add("sex", sex + "")
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("上传个人信息==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 3;
                handle.sendMessage(msg);
            }
        }.start();
    }

}
