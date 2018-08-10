package com.example.jqk.guitarassistant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LyricsView extends View {

    private ArrayList<String> lyrics;
    private GestureDetector gestureDetector;
    private int inViewHeight;
    private int viewHeight, viewWidth;
    private int scrllY = 0;
    private List<Integer> linesY;
    private List<Integer> baseLinesY, baseLinesYCopy;

    private int itemHeight;

    private int centerLineY;
    private Paint centerLinePaint;
    private Paint textPaint;

    private float mLastMotionY;

    public LyricsView(Context context) {
        super(context);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        lyrics = new ArrayList<String>();
        lyrics.add("让我掉下眼泪的 不止昨夜的酒");
        lyrics.add("让我依依不舍的 不止你的温柔");
        lyrics.add("余路还要走多久 你攥着我的手");
        lyrics.add("让我感到为难的 是挣扎的自由");
        lyrics.add("分别总是在九月 回忆是思念的愁");
        lyrics.add("深秋嫩绿的垂柳 亲吻着我额头");
        lyrics.add("在那座阴雨的小城里 我从未忘记你");
        lyrics.add("成都 带不走的 只有你");
        lyrics.add("和我在成都的街头走一走 喔…");
        lyrics.add("直到所有的灯都熄灭了也不停留");
        lyrics.add("你会挽着我的衣袖 我会把手揣进裤兜");
        lyrics.add("走到玉林路的尽头 坐在小酒馆的门口");

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.green));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50);

        centerLinePaint = new Paint();
        centerLinePaint.setColor(getResources().getColor(R.color.red));
        centerLinePaint.setStyle(Paint.Style.FILL);

        linesY = new ArrayList<Integer>();
        baseLinesY = new ArrayList<Integer>();
        baseLinesYCopy = new ArrayList<Integer>();

        gestureDetector = new GestureDetector(getContext(), new MyGestureDetector());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        itemHeight = viewHeight / 5;
        inViewHeight = itemHeight * lyrics.size();
        centerLineY = viewHeight / 2;

        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        for (int i = 0; i < lyrics.size(); i++) {
            linesY.add(i * itemHeight + itemHeight / 2);
            baseLinesY.add(i * itemHeight + itemHeight / 2 - (fm.bottom - fm.top) / 2 - fm.top);
            baseLinesYCopy.add(i * itemHeight + itemHeight / 2 - (fm.bottom - fm.top) / 2 - fm.top);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画歌词
        for (int i = 0; i < linesY.size(); i++) {
            canvas.drawText(lyrics.get(i), 0, baseLinesY.get(i), textPaint);
        }
        // 画中间线
        canvas.drawLine(0, centerLineY, viewWidth, centerLineY, centerLinePaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
//        float y = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastMotionY = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float delt = mLastMotionY - y;
//                mLastMotionY = y;
//                scrllY += (int) delt;
//                scrollBy(0, (int) delt);
//                break;
//
//            default:
//                break;
//        }


        return true;
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scroll((int) distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void scroll(int distanceY) {
        scrllY += distanceY;
        if (scrllY <= 0) {
            scrllY = 0;
            for (int i = 0; i < baseLinesY.size(); i++) {
                baseLinesY.set(i, baseLinesYCopy.get(i) - scrllY);
            }
            invalidate();
            return;
        }

        if (scrllY >= inViewHeight - viewHeight / 2) {
            scrllY = inViewHeight - viewHeight / 2;
            for (int i = 0; i < baseLinesY.size(); i++) {
                baseLinesY.set(i, baseLinesYCopy.get(i) - scrllY);
            }
            invalidate();
            return;
        }

        for (int i = 0; i < baseLinesY.size(); i++) {
            baseLinesY.set(i, baseLinesY.get(i) - distanceY);
        }
        invalidate();
    }

    public void jumpTo(int y) {
        scrollBy(0, y);
    }
}
