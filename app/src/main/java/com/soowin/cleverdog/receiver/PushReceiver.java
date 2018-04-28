package com.soowin.cleverdog.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.soowin.cleverdog.activity.welcome.HttpErrorActivity;
import com.soowin.cleverdog.utlis.PublicApplication;

import java.util.List;

/**
 * Created by hxt on 2017/8/29.
 * 百度推送回调广播监听
 */

public class PushReceiver extends PushMessageReceiver {
    public static final String TAG = PushReceiver.class.getSimpleName();

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
//        Log.e(TAG, "onBind: " + "String" + s + " String" + s1 + " String" + s2 + " String" + s3);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
//        Log.e(TAG, "onUnbind: " + "String" + s);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
//        Log.e(TAG, "onSetTags: " + "String" + s);
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
//        Log.e(TAG, "onDelTags: " + "String" + s);
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
//        Log.e(TAG, "onListTags: " + "String" + s);
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
//        Log.e(TAG, "onMessage: " + "String" + s + " String" + s1);
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
//        Log.e(TAG, "onNotificationClicked: " + "String" + s + " String" + s1 + " String" + s2);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
//        Log.e(TAG, "onNotificationArrived: " + "String" + s + " String" + s1 + " String" + s2);
    }
}
