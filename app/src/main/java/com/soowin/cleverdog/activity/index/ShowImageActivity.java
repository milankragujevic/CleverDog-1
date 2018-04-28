package com.soowin.cleverdog.activity.index;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.utlis.BaseActivity;

public class ShowImageActivity extends BaseActivity {
    public static final String URL = "url";
    private String url = "";
    private ImageView ivShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        url = getIntent().getStringExtra(URL);
        initView();

    }

    private void initView() {
        ivShow = findViewById(R.id.iv_show);
        Glide.with(this)
                .load(url)
                .error(R.drawable.img_null)
                .placeholder(R.drawable.img_null)
                .fitCenter()
                .into(ivShow);
    }
}
