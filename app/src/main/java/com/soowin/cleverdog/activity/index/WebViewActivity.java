package com.soowin.cleverdog.activity.index;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.welcome.WelcomeActivity;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 头条页面
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    final public static String URLS = "url";
    final public static String TITLE = "title";
    private String url;
    private String title;

    private TextView tvTitle;
    private ImageView ivBack;

    private WebView wvMyWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        url = this.getIntent().getStringExtra(URLS);
        title = this.getIntent().getStringExtra(TITLE);
        if (StrUtils.isEmpty(url))
            url = "http://222.223.239.214:802/?cat=3";
        initTitle();
        initView();
    }

    private void initView() {
        wvMyWv = findViewById(R.id.wv_my_wv);

        init();
    }

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);

        if (StrUtils.isEmpty(title))
            tvTitle.setText("头条");
        else
            tvTitle.setText(title);
        ivBack.setOnClickListener(this);
        ivBack.setVisibility(View.VISIBLE);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        wvMyWv.loadUrl(url);
        //设置编码
        wvMyWv.getSettings().setDefaultTextEncodingName("utf-8");
        //支持js
        wvMyWv.getSettings().setJavaScriptEnabled(true);

        wvMyWv.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        wvMyWv.getSettings().setSupportZoom(true);
        wvMyWv.getSettings().setBuiltInZoomControls(true);
        wvMyWv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvMyWv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvMyWv.getSettings().setDomStorageEnabled(true);
        wvMyWv.getSettings().setDatabaseEnabled(true);

        wvMyWv.setWebChromeClient(new WebChromeClient());// 设置浏览器可弹窗

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wvMyWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (wvMyWv.canGoBack())
                    wvMyWv.goBack();
                else
                    exitBy2Click();
                break;
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

    private void exitBy2Click() {
        if (ScreenManager.getScreenManager().getStackSize() > 1) {
            finish();
        } else {
            Intent intent = new Intent(PublicApplication.getContext(), WelcomeActivity.class);
            startActivity(intent);
            ScreenManager.getScreenManager().popActivity();
        }
    }
}
