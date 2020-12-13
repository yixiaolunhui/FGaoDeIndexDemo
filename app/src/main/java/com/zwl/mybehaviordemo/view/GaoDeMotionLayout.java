package com.zwl.mybehaviordemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;


import com.zwl.mybehaviordemo.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class GaoDeMotionLayout extends MotionLayout {
    /**
     * 初始位置
     */
    public final static float PROGRESS_START = 0f;
    /**
     * 顶部阀值
     */
    public final static float PROGRESS_TOP = 0.9f;
    /**
     * 低部阀值
     */
    public final static float PROGRESS_BOTTOM = 0.1f;
    /**
     * 中间位置
     */
    public final static float PROGRESS_MIDDLE = 0.6f;
    /**
     * 结束位置
     */
    public final static float PROGRESS_END = 1.0f;
    private float startY = 0;
    private boolean mTouchStared = false;
    private Disposable timeDisposable;
    /**
     * 有没有中间间断
     */
    private boolean hasMiddle;

    public GaoDeMotionLayout(Context context) {
        super(context);
    }


    public GaoDeMotionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setHasMiddle(boolean hasMiddle) {
        this.hasMiddle = hasMiddle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float progress = getProgress();
        View viewGroup = findViewById(R.id.content);
        Rect mRect = new Rect();
        if (!mTouchStared) {
            viewGroup.getHitRect(mRect);
            mTouchStared = mRect.contains((int) event.getX(), (int) event.getY());
        }
        if (timeDisposable != null){
            timeDisposable.dispose();
        }
        float endY;
        if (hasMiddle) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_CANCEL:
                    mTouchStared = false;
                    break;
                case MotionEvent.ACTION_DOWN:
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    endY = event.getY();
                    //手势向下
                    if ((endY - startY) > 0) {
                        if (progress >= PROGRESS_TOP) {
                            handleProgress(PROGRESS_END);

                        }
                        if (progress < PROGRESS_TOP && progress >= PROGRESS_MIDDLE) {
                            handleProgress(PROGRESS_MIDDLE);
                        }
                        if (progress < PROGRESS_MIDDLE) {
                            mTouchStared = false;
                            handleProgress(PROGRESS_START);
                        }
                        //手势向上
                    } else {
                        if (progress <= PROGRESS_BOTTOM) {
                            mTouchStared = false;
                            handleProgress(PROGRESS_START);
                        }
                        if (progress > PROGRESS_BOTTOM && progress <= PROGRESS_MIDDLE) {
                            handleProgress(PROGRESS_MIDDLE);
                        }
                        if (progress > PROGRESS_MIDDLE) {
                            handleProgress(PROGRESS_END);
                        }
                    }
                    return mTouchStared;
            }
        } else {
            if (event.getActionMasked() == MotionEvent.ACTION_CANCEL || event.getActionMasked() == MotionEvent.ACTION_UP) {
                mTouchStared = false;
                return super.onTouchEvent(event);
            }
        }
        return mTouchStared && super.onTouchEvent(event);

    }

    private void handleProgress(float progress) {
        //如果需要设置的进度和当前进度相同不做处理
        if (progress == getProgress()){
            return;
        }
        //动画播放时间底值
        final long time = 200;
        //进度间隔 >0 说明上拉 < 0说明下滑
        final float interval = progress - getProgress();
        final long startTime, endTime;
        if (interval > 0) {
            startTime = (long) (getProgress() * time);
            endTime = (long) (progress * time);
        } else {
            endTime = (long) (getProgress() * time);
            startTime = (long) (progress * time);
        }
        if (timeDisposable != null){
            timeDisposable.dispose();
        }
        //startTime 初始时间 endTime - startTime为次数 0为延迟时间 3为间隔 单位TimeUnit.MILLISECONDS 毫秒
        timeDisposable = Observable.intervalRange(startTime, endTime - startTime, 0, 3, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        //下滑需要反向
                        if (interval < 0) {
                            long interStart = aLong - startTime;
                            return endTime - interStart;
                        }
                        return aLong;
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        float pro = (Float.valueOf(aLong) / time);
                        setProgress(pro);
                    }
                });
    }



}
