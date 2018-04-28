package com.soowin.cleverdog.adapter.index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.index.MainActivity;
import com.soowin.cleverdog.utlis.PublicApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxt on 2017/9/7.
 */

public class VoiceAdapter extends BaseAdapter {
    private List<String> data = new ArrayList<>();
    LayoutInflater inflater = null;
    private Context context;

    public void setContext(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void setData(List<String> data) {
        this.data = data;
        MainActivity a = (MainActivity) context;
        if (a != null)
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_text_view, null);
            holder = new ViewHolder();
            holder.tvContent = view.findViewById(R.id.tv_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvContent.setText(data.get(i));
        return view;
    }

    static class ViewHolder {
        TextView tvContent;
    }
}
