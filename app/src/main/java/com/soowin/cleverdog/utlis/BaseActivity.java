package com.soowin.cleverdog.utlis;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.soowin.cleverdog.activity.welcome.WelcomeActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/8.
 */

public class BaseActivity extends Activity {
    private ProgressDialog dialog; // 正在加载进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        initDialog();
    }

    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         */
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    public void initDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("请稍等片刻...");
        dialog.setMessage("正在努力加载...");
        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            PublicApplication.mVoiceManage.addVoice20("灵狗持续为您服务！");
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManager.getScreenManager().popActivity(this);
        PublicApplication.mVoiceManage.delVoiceAll();
    }

}
