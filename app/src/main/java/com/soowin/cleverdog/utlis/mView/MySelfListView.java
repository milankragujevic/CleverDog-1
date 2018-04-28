package com.soowin.cleverdog.utlis.mView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/7/19.
 */

public class MySelfListView extends ListView {
    public MySelfListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private GestureDetector mGestureDetector;
    OnTouchListener mGestureListener;

    public MySelfListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return mGestureDetector.onTouchEvent(ev);

    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //判断是否上下滑动
            if (Math.abs(distanceY) / Math.abs(distanceX) > 2) {
                return true;//截获事件
            }
            return false;
        }

    }
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//
//
//        intexpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//
//
//
//                MeasureSpec.AT_MOST);
//
//
//        super.onMeasure(widthMeasureSpec, expandSpec);
//
//
//    }
}
