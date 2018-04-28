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
import com.soowin.cleverdog.info.login.RegisterBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.R.attr.handle;

public class EditPassWordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private TextView tvTitle;
    private ImageView ivBack;

    private EditText etOld;
    private EditText etNew;
    private EditText etConfirm;
    private CheckBox cbOld;
    private CheckBox cbNew;
    private CheckBox cbConfirm;

    private TextView tvSubmit;

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        dismissDialog();
                        if (HttpTool.isHttpError(msg, null, getApplicationContext())) {
                            return;
                        } else if (HttpTool.isHttpNull(msg, null, getApplicationContext())) {
                            return;
                        } else {
                            Gson gson = new Gson();
                            IsOkBean dataBean = new IsOkBean();
                            dataBean = gson.fromJson(msg.obj.toString(),
                                    IsOkBean.class);
                            int state = dataBean.getState();
                            switch (state) {
                                case 1:
                                    showToast(dataBean.getMessage());
                                    finish();
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(EditPassWordActivity.this, "网络连接错误！请稍后重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pass_word);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        initTitle();
        initView();
    }

    private void initView() {
        etOld = findViewById(R.id.et_old);
        etNew = findViewById(R.id.et_new);
        etConfirm = findViewById(R.id.et_confirm);
        cbOld = findViewById(R.id.cb_old);
        cbNew = findViewById(R.id.cb_new);
        cbConfirm = findViewById(R.id.cb_confirm);
        tvSubmit = findViewById(R.id.tv_submit);

        cbOld.setOnCheckedChangeListener(checkedListener);
        cbNew.setOnCheckedChangeListener(checkedListener);
        cbConfirm.setOnCheckedChangeListener(checkedListener);
        tvSubmit.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener checkedListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.cb_old:
                            if (b)
                                etOld.setInputType(InputType.TYPE_CLASS_TEXT);
                            else
                                etOld.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            break;
                        case R.id.cb_new:
                            if (b)
                                etNew.setInputType(InputType.TYPE_CLASS_TEXT);
                            else
                                etNew.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            break;
                        case R.id.cb_confirm:
                            if (b)
                                etConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                            else
                                etConfirm.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            break;
                    }
                }
            };

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);

        tvTitle.setText("修改密码");
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                submit();
                break;
        }
    }

    /**
     * 提交修改密码
     */
    private void submit() {
        String strOld = etOld.getText().toString().trim();
        String strNew = etNew.getText().toString().trim();
        String strConfirm = etConfirm.getText().toString().trim();

        if (StrUtils.isEmpty(strOld) || StrUtils.isEmpty(strNew) || StrUtils.isEmpty(strConfirm)) {
            showToast("您还有未填写项");
        } else if (!strConfirm.equals(strNew)) {
            showToast("两次新密码不一致");
        } else {
            editPw(strOld, strNew);
        }
    }

    /**
     * 修改密码
     *
     * @param strOld
     * @param strNew
     */
    private void editPw(final String strOld, final String strNew) {
        showDialog();
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.modifyPassword)
                        .add("passwd", strOld)
                        .add("new_pwd", strNew)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("修改密码==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }
}
