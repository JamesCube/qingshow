package com.focosee.qingshow.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import com.huewu.pla.lib.internal.PLA_AbsListView;

/**
 * Created by Administrator on 2015/3/5.
 */
public class HeadScrollAdapter implements AbsListView.OnScrollListener, View.OnTouchListener, PLA_AbsListView.OnScrollListener {
    private Context context;
    private RelativeLayout headRelativeLayout;
    private int firstVisibleItem;
    private int padding;
    public int headHeight;
    private boolean isHeadMove = false;

    public HeadScrollAdapter(RelativeLayout headRelativeLayout, Context context) {
        this.headRelativeLayout = headRelativeLayout;
        this.context = context;
        headHeight = headRelativeLayout.getLayoutParams().height;
    }

    public void setHeadY(float Y){
        headRelativeLayout.setY(Y);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(null == view.getChildAt(1)) return;

        if(firstVisibleItem != 0){
            if(headRelativeLayout.getY() != (float)-headHeight) {
                headRelativeLayout.setY((float)-headHeight);
            }
            return;
        }
        headRelativeLayout.setY((view.getChildAt(1).getY() - headRelativeLayout.getLayoutParams().height) > 0 ? 0 : view.getChildAt(1).getY() - headRelativeLayout.getLayoutParams().height);
    }

    private VelocityTracker mVelocityTracker;

    private float speedTouch;
    private float offSetTouch;
    private float maxVelocity = 20;
    private float direction = 500;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if (((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin != 0) {
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//            params.setMargins(0, 0, 0, 0);
//            v.setLayoutParams(params);
//            v.setPadding(0, headHeight, 0, 0);
//            isHeadMove = false;
//        }
//        if (null == mVelocityTracker) {
//            mVelocityTracker = VelocityTracker.obtain();
//            isHeadMove = true;
//        }
//
//        mVelocityTracker.addMovement(event);
//
//        final VelocityTracker velocityTracker = mVelocityTracker;
//        // 1000 provides pixels per second
//        velocityTracker.computeCurrentVelocity(1, maxVelocity); //设置maxVelocity值为0.1时，速率大于0.01时，显示的速率都是0.01,速率小于0.01时，显示正常
//        speedTouch = velocityTracker.getYVelocity();
//
//        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
//        offSetTouch = velocityTracker.getYVelocity();
//        if (offSetTouch > 0 && offSetTouch > direction) {//向下滑动
//            if (speedTouch == maxVelocity) {//快速滑动
//                headRelativeLayout.setY(0);
//                padding = headHeight;
//                v.setPadding(0, padding, 0, 0);
//                isHeadMove = false;
//            } else {//触摸滑动
//                if (this.firstVisibleItem == 0) {
//                    headRelativeLayout.setY(0);
//                    padding = headHeight;
//                    v.setPadding(0, padding, 0, 0);
//                    isHeadMove = false;
//                }
//            }
//
//        } else if (offSetTouch < -direction) {//向上滑动
//            isHeadMove = true;
//        }
        return false;
    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

    }

    boolean firstScroll = true;

    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if(null == view.getChildAt(2)){
            return;
        }

        if(firstVisibleItem != 0){
            if(headRelativeLayout.getY() != (float)-headHeight){
                headRelativeLayout.setY((float)-headHeight);
            }
            return;
        }
//        this.firstVisibleItem = firstVisibleItem;
//        padding = headRelativeLayout.getLayoutParams().height + (int) headRelativeLayout.getY();
//        padding = padding > headHeight ? headHeight : padding;
//        padding = padding < 0 ? 0 : padding;
//
//        if (true) {
//            if (true) {
//                if (null != view.getChildAt(2)) {//第一次滚动时，设置下偏移的第一，二项的位置
//                    ((View) view.getChildAt(2)).setTop(headHeight);
//                    if (null != view.getChildAt(3)) {
//                        ((View) view.getChildAt(3)).setTop(headHeight);
//                    }
//                    firstScroll = false;
//                    return;
//                }
//            }
//            if (null != view.getChildAt(2)) {
//                if (padding > 0 && padding <= headHeight) {
//                    view.setPadding(0, padding, 0, 0);
                    //head根据第一项的位置变化而变化
                    headRelativeLayout.setY((((View)view.getChildAt(2)).getY() - headHeight) > 0 ? 0 : ((View)view.getChildAt(2)).getY() - headHeight);
//                } else {
//                    isHeadMove = false;
//
//                    if (view.getPaddingTop() != 0 && padding == 0)
//                        view.setPadding(0, padding, 0, 0);
//                }
//            }
//        }
    }
}