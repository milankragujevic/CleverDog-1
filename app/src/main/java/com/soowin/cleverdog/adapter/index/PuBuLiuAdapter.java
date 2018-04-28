package com.soowin.cleverdog.adapter.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.info.index.TicketListBean;
import com.soowin.cleverdog.utlis.Utlis;
import com.soowin.cleverdog.utlis.mInterFace.MyItemClickListener;

import java.util.List;

/**
 * Created by hxt on 2017/6/21.
 * 商品瀑布流列表
 */

public class PuBuLiuAdapter extends RecyclerView.Adapter<PuBuLiuAdapter.MyHolder> {
    LayoutInflater inflater = null;
    private List<TicketListBean.ResultBean.DataBean> data;
    private Context context;
    private MyItemClickListener mOnItemClickListener;
    private int width = 0;

    public PuBuLiuAdapter(Context context, List<TicketListBean.ResultBean.DataBean> data) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.context = context;
    }

    public PuBuLiuAdapter(Context context, int width) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.width = width;
    }

    public void setData(List<TicketListBean.ResultBean.DataBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 创建条目布局
     */
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_good_list, null);
        return new MyHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(MyHolder myHolder, final int position) {
        myHolder.setDataAndRefreshUI(data.get(position));
        myHolder.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, data.get(position).getThumb().replace(" ", ""));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;
        private TextView tvTime;
        private ImageView ivContent;

        public MyHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivContent = itemView.findViewById(R.id.iv_content);
        }

        public void setDataAndRefreshUI(TicketListBean.ResultBean.DataBean data) {
            /*Glide.with(context)//activty
                    .load(Utlis.getAvatar(data.getThumb()).replace(" ", ""))
                    .asBitmap()
                    .placeholder(R.drawable.img_null)
                    .error(R.drawable.img_null)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                              @Override
                              public void onResourceReady(Bitmap resource,
                                                          GlideAnimation<? super Bitmap> glideAnimation) {
                                  int h2 = width * 3 / 4;
                                  ivContent.setLayoutParams(
                                          new LinearLayout.LayoutParams(
                                                  ViewGroup.LayoutParams.MATCH_PARENT, h2));
                                  ivContent.setImageBitmap(resource);
                              }
                          }
                    );*/
            Glide.with(context)//activty
                    .load(Utlis.getAvatar(data.getThumb()).replace(" ", ""))
                    .placeholder(R.drawable.img_null)
                    .error(R.drawable.img_null)
                    .centerCrop()
                    .into(ivContent);
            ivContent.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    width * 9 / 16));
            tvContent.setText(data.getContent());
            tvTime.setText(data.getAdd_time());
        }
    }
}
