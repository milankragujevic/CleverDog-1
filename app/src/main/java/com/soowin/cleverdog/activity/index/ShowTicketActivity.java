package com.soowin.cleverdog.activity.index;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.adapter.index.PuBuLiuAdapter;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.TicketListBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.Utlis;
import com.soowin.cleverdog.utlis.mInterFace.MyItemClickListener;
import com.soowin.cleverdog.utlis.mView.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 晒罚单页面
 */
public class ShowTicketActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ShowTicketActivity.class.getSimpleName();

    private int width;

    private int PAGE = 1;
    private boolean isHaveUp = true;//是否需要上拉
    private boolean ISOKHTTP = true;

    private TextView tvTitel;
    private ImageView ivBack;
    private ImageView ivCamera;

    private SwipeRefreshLayout srlMsrl;
    private RecyclerView rvContent;
    private StaggeredGridLayoutManager SGLM;
    private MyDividerItemDecoration MDID;
    private List<TicketListBean.ResultBean.DataBean> mData = new ArrayList<>();//数据源
    private PuBuLiuAdapter mAdapter;

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    srlMsrl.setRefreshing(false);
                    if (msg.what == 1) {
                        Gson gson = new Gson();
                        TicketListBean dataBean = new TicketListBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                TicketListBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                if (PAGE == 1)
                                    mData.clear();
                                List<TicketListBean.ResultBean.DataBean> newData = dataBean.getResult().getData();
                                int totalPage = dataBean.getResult().getPage().getTotalpage();
                                int p = dataBean.getResult().getPage().getPageNo();
                                if (newData != null)
                                    if (newData.size() > 0)
                                        if (PAGE <= p)
                                            mData.addAll(newData);
                                mAdapter.setData(mData);
                                PAGE++;//下次请求页码加一
                                if (PAGE >= totalPage)//和总页数对比判断是否需要加载更多
                                    isHaveUp = false;
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ShowTicketActivity", e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticket);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        initWindow();
        initTitle();
        initView();
    }

    private void initWindow() {
        WindowManager wm = getWindowManager();
        // 重设高度
        width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ISOKHTTP = true;
        initData();
    }

    private void initData() {
        srlMsrl.setRefreshing(true);
        PAGE = 1;
        getData();
    }

    /**
     * 网络请求数据
     */
    private void getData() {
        new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.TicketList)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("罚单列表==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    private void initView() {
        srlMsrl = findViewById(R.id.srl_mSrl);
        rvContent = findViewById(R.id.rv_content);

        srlMsrl.setColorSchemeColors(Color.BLUE, Color.RED);
        srlMsrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        //瀑布流适配manager
        SGLM = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvContent.removeItemDecoration(MDID);
        MDID = new MyDividerItemDecoration(this, 10,
                getResources().getColor(R.color.white));
        rvContent.addItemDecoration(MDID);
        //绑定manager
        rvContent.setLayoutManager(SGLM);
        mAdapter = new PuBuLiuAdapter(this, width);
        mAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, String id) {
                Intent intent = new Intent(ShowTicketActivity.this, ShowImageActivity.class);
                intent.putExtra(ShowImageActivity.URL, id);
                startActivity(intent);
            }
        });
        rvContent.setAdapter(mAdapter);
        //滑动监听
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //判断是否到达屏幕底部
                if (Utlis.isSlideToBottom(recyclerView)) {
                    if (isHaveUp) {
                        getData();
                    }
                } else {
                    isHaveUp = true;
                }
            }
        });
    }

    private void initTitle() {
        tvTitel = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivCamera = findViewById(R.id.iv_camera);

        tvTitel.setText("晒罚单");
        ivBack.setVisibility(View.VISIBLE);
        ivCamera.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                Intent intent = new Intent(this, ReleaseTicketActivity.class);
                startActivity(intent);
                break;
        }
    }

}
