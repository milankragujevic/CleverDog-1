package com.soowin.cleverdog.activity.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.index.MainActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.ScreenManager;

import java.util.Timer;
import java.util.TimerTask;

public class HttpErrorActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = HttpErrorActivity.class.getSimpleName();
    private boolean isHttpOk = false;

    private TextView tvError;

    private Thread httpInspect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_error);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈

        initView();
        initHttpInspect();
    }

    /**
     * 初始化网络检查功能
     */
    private void initHttpInspect() {
        httpInspect = new Thread() {
            @Override
            public void run() {
                while (!isHttpOk) {
                    try {
//                        httpIsOk();
                        sleep(2000);//每秒一次检查网络状态
                        Log.e(TAG, "run: 检查网络子线程");
                        if (HttpTool.isOpenNetwork(HttpErrorActivity.this)) {
                            startActivity(new Intent(HttpErrorActivity.this, MainActivity.class));
                            isHttpOk = true;
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        httpInspect.start();
    }

    private void initView() {
        tvError = findViewById(R.id.tv_error);

        tvError.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_error:
                httpIsOk();
                break;
        }
    }

    private void httpIsOk() {
        if (HttpTool.isOpenNetwork(HttpErrorActivity.this)) {
            startActivity(new Intent(HttpErrorActivity.this, MainActivity.class));
            finish();
        } else {
            showToast("网络异常！请稍后重试");
        }
    }

    @Override
    protected void onDestroy() {
        isHttpOk = true;
        super.onDestroy();
    }
}
