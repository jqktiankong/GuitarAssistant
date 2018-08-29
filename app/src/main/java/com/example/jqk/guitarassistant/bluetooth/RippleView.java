package com.example.jqk.guitarassistant.bluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 思路：动态画圈，然后循环刷新view，圆半径每次加1，透明度每次减1，最大透明度=255 - 圆最大半径；根据时间定时添加圆
 * 缺点：如果圆半径小于255，圆不是从255开始淡出的, 从空间宽度的一般开始淡出
 * 提示：circleNumber跟newCircleTime要设置好才能有好的效果
 */

public class RippleView extends View {
    // view的宽高
    private int dialWidth, dialHeight;
    // 画笔
    private Paint paint;
    // 圆的最大半径
    private float maxWidth;
    // 是否运行
    private boolean isStarting = false;
    // 透明度list
    private List<Float> alphaList = new ArrayList<Float>();
    // 圆半径list
    private List<Float> startWidthList = new ArrayList<Float>();
    // 圆的圈数 默认为10
    private int circleNumber = 4;
    // 刷新速度
    private int speed = 1;
    // 扩散速度
    private long refreshSpeed = 10;

    private long createTime = 0;

    private long lastCreateTime = 0;
    // 隔time添加新圆
    private long newCircleTime = 200;
    // 颜色
    private int color;
    // 扩散单位
    private float diffusionUnit = 1;

    private float zoomOutUnit = 1;
    // 第一次添加圆
    private boolean firstAdd = true;
    // 添加圆
    private boolean add = true;
    // 发送延时任务
    private Handler handler = new Handler();
    // 循环任务
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 定时刷新界面
            invalidate();
            handler.postDelayed(this, refreshSpeed);
        }
    };

    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(); // 设置博文的颜色
        color = 0x0059ccf5;
        paint.setColor(color);
    }

    public void setCircleNumber(int circleNumber) {
        this.circleNumber = circleNumber;
    }

    public void setColor(int colorId) {
        paint.setColor(getResources().getColor(colorId));
    }

    public void setColor(String color) {
        paint.setColor(Color.parseColor(color));
    }

    public void setNewCircleTime(int newCircleTime) {
        this.newCircleTime = newCircleTime;
    }

    /**
     * @param refreshSpeed 参数越大扩散越慢
     */
    public void setRefreshSpeed(long refreshSpeed) {
        this.refreshSpeed = refreshSpeed;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthSpecMode) {
            case MeasureSpec.UNSPECIFIED:// 未确认 测量规范模式：父项没有对子项施加任何约束。 它可以是任何它想要的大小。
                dialWidth = 20;
                break;
            case MeasureSpec.AT_MOST: // 究竟 测量规格模式：父母已确定孩子的确切大小。 孩子将被给予那些边界，无论它想要多大。
            case MeasureSpec.EXACTLY: // 最多 测量规格模式：孩子可以大到它想要达到指定的尺寸。
                dialWidth = widthSpecSize;
                break;
        }
        switch (heightSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                dialHeight = 20;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                dialHeight = heightSpecSize;
                break;
        }

        if (dialWidth < dialHeight) {
            dialHeight = dialWidth;
        } else {
            dialWidth = dialHeight;
        }

        setMeasuredDimension(dialWidth, dialHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        maxWidth = dialWidth / 2.0f;

        diffusionUnit = (maxWidth > 255.0f ? maxWidth / 255.0f : 1);
        zoomOutUnit = (maxWidth > 255.0f ? 1 : 255.0f / maxWidth);

        // 颜色：完全透明
//        setBackgroundColor(Color.TRANSPARENT);
        // 依次绘制 同心圆
        for (int i = 0; i < alphaList.size(); i++) {
            // 透明度
            float alpha = alphaList.get(i);
            // 圆半径
            float startWidth = startWidthList.get(i);

            paint.setAlpha((int) (alpha + 0.5));
            // 这个半径决定你想要多大的扩散面积
            if (isStarting) {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, startWidth, paint);
            }
            // 同心圆扩散
            if (isStarting && startWidth <= maxWidth - diffusionUnit * speed) {
                alphaList.set(i, alpha - zoomOutUnit * speed > 0 ? alpha - zoomOutUnit * speed : 0.0f); // 透明度-1
                startWidthList.set(i, (startWidth + diffusionUnit * speed)); // 半径+扩散单位
            }
        }
        // 是否添加新的圆 按时间增加

        createTime = System.currentTimeMillis();

        if (createTime - lastCreateTime >= newCircleTime) {
            lastCreateTime = createTime;
            add = true;
        } else {
            add = false;
        }
        // 平均添加圆
        if (isStarting && add) {
            alphaList.add(255.0f);
            startWidthList.add(0f);
        }
        // 同心圆数量达到10个，删除最外层圆
        if (isStarting && startWidthList.size() == circleNumber) {
            startWidthList.remove(0);
            alphaList.remove(0);
        }
    }

    // 执行动画
    public void start() {

        if (!isStarting) {
            isStarting = true;
            lastCreateTime = 0;
            handler.postDelayed(runnable, refreshSpeed);
        }
    }

    // 停止动画
    public void stop() {

        if (isStarting()) {
            isStarting = false;
            lastCreateTime = 0;
            handler.removeCallbacks(runnable);
            firstAdd = true;
            startWidthList.clear();
            alphaList.clear();
            invalidate();
        }
    }

    // 判断是不是在扩散
    public boolean isStarting() {
        return isStarting;
    }
}
