package com.soowin.cleverdog.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.login.LoginBean;
import com.soowin.cleverdog.info.login.WXLoginBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.SettingUtlis;
import com.soowin.cleverdog.utlis.StrUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class WXEntryActivity extends BaseActivity {
    private String WEIXIN_APP_ID = "wx03d51cfead473cd9";
    private String SECRET = "c83c3acb0bdec2515c2e1bf8901869d3";

    String wxHttpUrl = "";
    private String openid = "";
    private String unionid = "";

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        Gson gson = new Gson();
                        WXLoginBean dataBean = new WXLoginBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                WXLoginBean.class);
                        openid = dataBean.getOpenid();
                        unionid = dataBean.getUnionid();
                        getLoginInfo(dataBean.getOpenid(), dataBean.getUnionid());
                    } else if (msg.what == 2) {
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

                                if (dataBean.getResult().getUser_status().equals("1"))
                                    PublicApplication.WXLogin = 3;
                                else {
                                    int myV = PublicApplication.db.getVersion();
                                    int webV = Integer.parseInt(PublicApplication.loginInfo.getString("versionnumber", ""));
                                    if (myV != webV) {
                                        Intent mIntent = new Intent();
                                        mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
                                        mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                                        startService(mIntent);
                                    }
                                    showToast("微信登录成功");
                                    PublicApplication.WXLogin = 1;
                                }
                                finish();
                                break;
                            case 2:
                                PublicApplication.WXLogin = 2;
                                PublicApplication.WXopenid = openid;
                                PublicApplication.WXunionid = unionid;
                                finish();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("WXEntryActivity", e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (PublicApplication.isLoginCallback) {
            String result = "";
            int i = 0;//1、登录成功 2、取消登录 0、登录失败
            SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = "登录成功";
                    i = 1;
                    wxHttpUrl = "?appid=" + WEIXIN_APP_ID
                            + "&secret=" + SECRET + "&code=" + resp.code + "&grant_type=authorization_code";
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = "取消登录";
                    i = 0;
                    break;
                default:
                    result = "登录失败";
                    i = 0;
                    break;
            }
            if (i == 1)
                getOpenId();
            else {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            finish();
        }
    }

    public void getOpenId() {
        showDialog();
        if (!StrUtils.isEmpty(wxHttpUrl)) {
            new Thread() {
                @Override
                public void run() {
                    HttpTool httpTool = new HttpTool();
                    String result = httpTool.httpGet(
                            "https://api.weixin.qq.com/sns/oauth2/access_token",
                            wxHttpUrl);
                    Log.e("微信请求openId==..result.=", result + "");
                    Message msg = handle.obtainMessage();
                    msg.obj = result;
                    msg.what = 1;
                    handle.sendMessage(msg);
                }
            }.start();
        }
    }

    /**
     * 获取本服务器登录状态
     *
     * @param openid
     * @param unionid
     */
    public void getLoginInfo(final String openid, final String unionid) {
        new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&openid=" + openid
                       *//* + "&password=" + Utlis.toBase64(unionid)*//*;
                String result = httpTool.httpPost(
                        PublicApplication.urlData.third_login,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.third_login)
                        .add("openid", openid)
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("微信登录==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        }.start();
    }
}