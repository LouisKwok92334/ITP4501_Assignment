package com.example.itp4501_assignment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//以下的都是gpt教學，大約問題會是
//1. 我要如何在android studio弄一個不斷出現星星的背景動畫？
//2. 根據你提供給我的代碼，我要怎樣調整為不同的顏色、數量、以及大小等等？
//3. 幫我弄得再好看點 or 我想把數量減少

public class FallingStarView extends View {
    // 自定義的類型，用於表示每顆星星的位置和速度等屬性
    private static class Star {
        PointF position;
        PointF velocity;
        float size;
        int color;

        Star(float x, float y, float vx, float vy, float size, int color) {
            position = new PointF(x, y);
            velocity = new PointF(vx, vy);
            this.size = size;
            this.color = color;
        }
    }

    private Paint paint;
    private List<Star> stars;
    private Random random;
    private int viewWidth;
    private int viewHeight;
    private static final int STAR_NUM = 8;

    public FallingStarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        stars = new ArrayList<>();
        random = new Random();
        init();
    }

    private int[] colors = {
            0xFFFF0000, // 红色
            0xFF00FF00, // 绿色
            0xFF0000FF, // 蓝色
            0xFFFFFF00, // 黄色
            0xFFFF00FF, // 品红
            0xFF00FFFF  // 青色
    };

    private void init() {
        paint.setColor(0xFFFFFFFF); // 設置畫筆顏色為白色
        paint.setAntiAlias(true); // 設置抗鋸齒效果
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        generateStars();
    }

    private void generateStars() {
        for (int i = 0; i < STAR_NUM; i++) {
            float x = random.nextFloat() * viewWidth;
            float y = viewHeight; // 将星星的初始 y 坐标更改为屏幕底部
            float angle = (float) (Math.PI * (random.nextFloat() * 0.25 + 0.25)); // 产生45°至135°之间的随机角度
            float speed = 2 + random.nextFloat() * 5;
            float vx = (float) (speed * Math.cos(angle));
            float vy = (float) (-speed * Math.sin(angle));
            float size = 5 + random.nextFloat() * 10; //
            int colorIndex = random.nextInt(colors.length);
            int color = colors[colorIndex];
            stars.add(new Star(x, y, vx, vy, size, color));
        }
    }

    public void updateStars() {
        List<Star> toRemove = new ArrayList<>();
        for (Star star : stars) {
            star.position.x += star.velocity.x;
            star.position.y += star.velocity.y;

            if (star.position.y < 0 || star.position.x < 0 || star.position.x > viewWidth) {
                toRemove.add(star);
            }
        }
        stars.removeAll(toRemove);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Star star : stars) {
            int alpha = (int) (255 * (1 - star.position.y / viewHeight)); // 根据 y 坐标调整透明度
            paint.setAlpha(alpha);
            drawStar(canvas, star.position.x, star.position.y, star.size, star.color);
        }
    }

    public void startAnimation() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                updateStars();
                if (stars.size() < STAR_NUM) {
                    int numToAdd = STAR_NUM - stars.size();
                    for (int i = 0; i < numToAdd; i++) {
                        float x = random.nextFloat() * viewWidth;
                        float y = viewHeight;
                        float angle = (float) (Math.PI * (random.nextFloat() * 0.25 + 0.25)); // 产生45°至135°之间的随机角度
                        float speed = 2 + random.nextFloat() * 5;
                        float vx = (float) (speed * Math.cos(angle));
                        float vy = (float) (-speed * Math.sin(angle));
                        float size = 5 + random.nextFloat() * 10;
                        int colorIndex = random.nextInt(colors.length);
                        int color = colors[colorIndex];
                        stars.add(new Star(x, y, vx, vy, size, color));
                    }
                }
                sendEmptyMessageDelayed(0, 16);
            }
        };
        handler.sendEmptyMessage(0);
    }

    private void drawStar(Canvas canvas, float x, float y, float radius, int color) {
        paint.setColor(color);
        double angle = 2 * Math.PI / 5;
        Path path = new Path();
        for (int i = 0; i < 5; i++) {
            float innerX = (float) (x + radius * 0.4 * Math.cos((i * 2) * angle - Math.PI / 2));
            float innerY = (float) (y + radius * 0.4 * Math.sin((i * 2) * angle - Math.PI / 2));
            float outerX = (float) (x + radius * Math.cos((i * 2 + 1) * angle - Math.PI / 2));
            float outerY = (float) (y + radius * Math.sin((i * 2 + 1) * angle - Math.PI / 2));
            if (i == 0) {
                path.moveTo(outerX, outerY);
            } else {
                path.lineTo(outerX, outerY);
            }
            path.lineTo(innerX, innerY);
        }
        path.close();
        canvas.drawPath(path, paint);
    }
}