package com.soowin.cleverdog.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.soowin.cleverdog.dataBase.DBHelper;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.TicketListBean;
import com.soowin.cleverdog.info.index.ViolationListBean;
import com.soowin.cleverdog.utlis.MapUtlis;
import com.soowin.cleverdog.utlis.PublicApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by hxt on 2017/9/14.
 * 更新数据库 服务
 */

public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();
    Lock mLock = new ReentrantLock();//线程锁

    private int PAGE = 1;
    private boolean isHaveUp = false;//是否需要上拉
    private boolean ISHTTPING = false;//是否正在更新数据库

    private List<ViolationListBean.ResultBean.DataBean> mData = new ArrayList<>();

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        Gson gson = new Gson();
                        ViolationListBean dataBean = new ViolationListBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                ViolationListBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                if (PAGE == 1)
                                    mData.clear();
                                List<ViolationListBean.ResultBean.DataBean> newData = dataBean.getResult().getData();
                                int totalPage = dataBean.getResult().getPage().getTotalpage();
                                int p = Integer.parseInt(dataBean.getResult().getPage().getPageNo());
                                if (newData != null)
                                    if (newData.size() > 0)
                                        if (PAGE <= p)
                                            mData.addAll(newData);
                                PAGE++;//下次请求页码加一
                                if (PAGE <= totalPage)//和总页数对比判断是否需要加载更多
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            getData();
                                        }
                                    }.start();
                                else {
                                    PublicApplication.db.setVersion(Integer.parseInt(dataBean.getResult().getVersionnumber()));
                                    addDataBase();
                                }
                                break;
                            case 2:
                                if (dataBean.getResult().getVersionnumber() != null)
                                    PublicApplication.db.setVersion(Integer.parseInt(dataBean.getResult().getVersionnumber()));
                                addDataBase();
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downloadStar() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (ISHTTPING)
                        ISHTTPING = true;
                    getData();
                } finally {
                }
            }
        }.start();
    }

    /**
     * 数据库添加数据
     */
    private void addDataBase() {
        new Thread() {
            @Override
            public void run() {
                try {
                    PublicApplication.db.deleteAllData(DBHelper.MAIN_TABLE);//删除所有数据
                    for (int i = 0; i < mData.size(); i++) {
                        String[] position = mData.get(i).getPosition().split(",");
                        ContentValues values = new ContentValues();
                        values.put("denoter_id", mData.get(i).getId());
                        values.put("denoter_type", getType(mData.get(i).getSigns()));
                        values.put("star_time", MapUtlis.timedate(mData.get(i).getStart_time()));
                        values.put("end_time", MapUtlis.timedate(mData.get(i).getEnd_time()));
                        values.put("denoter_name",mData.get(i).getSection());
                        values.put("latitude", position[1]);
                        values.put("longitude", position[0]);

                        PublicApplication.db.insert(DBHelper.MAIN_TABLE, values);
                    }
                } finally {
                    Intent mIntent = new Intent();
                    mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
                    mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                    stopService(mIntent);
                }
            }
        }.start();
        ISHTTPING = false;
    }

    private int getType(String str) {
        //（cmpi.车让人违章
        //rslv.轧实线违章
        //ygv.黄色网格违章
        //sluv.直行道掉头违章
        //wv.鸣笛违章
        //hbv.远光灯违章
        //nstop,禁止停车）
        //other,特殊类型播报）
        int i = 0;
        switch (str) {
            case "cmpi":
                i = 1;
                break;
            case "rslv":
                i = 2;
                break;
            case "ygv":
                i = 3;
                break;
            case "sluv":
                i = 4;
                break;
            case "wv":
                i = 5;
                break;
            case "hbv":
                i = 6;
                break;
            case "nstop":
                i = 7;
                break;
            case "other":
                i = 8;
                break;
        }
        return i;
    }

    /**
     * 获取最新数据
     */
    public void getData() {
        RequestBody body = new FormBody.Builder()
                .add("json", PublicApplication.urlData.violationList)
                .add("page", PAGE + "")
                .add("pagesize", 20 + "")
                .build();
        String result = HttpTool.okPost(body);
        Log.e("违章点列表==..result.=", result + "");
        Message msg = handle.obtainMessage();
        msg.obj = result;
        msg.what = 1;
        handle.sendMessage(msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadStar();
        return super.onStartCommand(intent, flags, startId);
    }
}
