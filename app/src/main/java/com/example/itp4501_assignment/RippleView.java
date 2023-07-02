package com.example.itp4501_assignment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RippleView extends View {

    private Paint mPaint;
    private float[] mRadii;
    private float mCenterX;
    private float mCenterY;
    private long mStartTime;
    private static final int RIPPLE_DURATION = 400; // 修改波纹持续时间
    private static final int NUM_RIPPLES = 2; // 减少波纹数量
    private static final int RIPPLE_DELAY = 40; // 修改波纹之间的间隔
    private MediaPlayer mMediaPlayer;

    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void playSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.ripple_color, null));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6); // 增加画笔宽度
        mMediaPlayer = MediaPlayer.create(getContext(), R.raw.click);
        mRadii = new float[NUM_RIPPLES];
        setWillNotDraw(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStartTime = System.currentTimeMillis();
            mCenterX = event.getX();
            mCenterY = event.getY();
            playSound();
            for (int i = 0; i < NUM_RIPPLES; i++) {
                mRadii[i] = -i * RIPPLE_DELAY;
            }
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long elapsedTime = System.currentTimeMillis() - mStartTime;
        for (int i = 0; i < NUM_RIPPLES; i++) {
            if (elapsedTime - i * RIPPLE_DELAY <= RIPPLE_DURATION) {
                mRadii[i] += 8; // 增加波纹扩散速度
                mPaint.setAlpha(255 - (int) (255 * (elapsedTime - i * RIPPLE_DELAY) / (RIPPLE_DURATION * 2))); // 修改透明度变化速度
                canvas.drawCircle(mCenterX, mCenterY, mRadii[i], mPaint);
            }
        }
        if (elapsedTime <= RIPPLE_DURATION + (NUM_RIPPLES - 1) * RIPPLE_DELAY) {
            postInvalidateDelayed(10);
        }
    }
}