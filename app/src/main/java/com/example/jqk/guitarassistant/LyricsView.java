package com.example.jqk.guitarassistant;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LyricsView extends View {

    private List<Lyric> lyrics;
    private List<Lyric> lyricsCopy;
    private List<Tune> tunes;
    private List<Tune> tunesCopy;
    private GestureDetector gestureDetector;
    private int inViewHeight;
    private int viewHeight, viewWidth;
    private int scrllY = 0;

    private int playNum = 26;

    private int itemHeight;

    private int centerLineY;
    private Paint centerLinePaint;
    private Paint lyricPaint;
    private Paint lyricSelectPaint;
    private Paint turnPaint;
    private Paint turnSelectPaint;
    private Paint playedPaint;

    private boolean isScrolling = false;

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
        lyrics = new ArrayList<Lyric>();
        lyricsCopy = new ArrayList<Lyric>();
        String string = "----作词:赵雷----"
                + "----作曲:赵雷----"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒"
                + "让我掉下眼泪的不只昨夜的酒";
        for (char c : string.toCharArray()) {
            Lyric lyric = new Lyric();
            lyric.setLyric(c);
            lyric.setPlayed(false);
            lyrics.add(lyric);

            Lyric lyric1 = new Lyric();
            lyric1.setLyric(c);
            lyric1.setPlayed(false);
            lyricsCopy.add(lyric1);
        }

        tunes = new ArrayList<Tune>();
        tunesCopy = new ArrayList<Tune>();

        String str = "............."
                + "............."
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165"
                + "5112533512165";
        for (char c : str.toCharArray()) {
            Tune tune = new Tune();
            tune.setTune(c);
            tune.setPlayed(false);
            tunes.add(tune);

            Tune tune1 = new Tune();
            tune1.setTune(c);
            tune1.setPlayed(false);
            tunesCopy.add(tune1);
        }

        // 歌词笔触
        lyricPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lyricPaint.setColor(getResources().getColor(R.color.colorlyric));
        lyricPaint.setStyle(Paint.Style.FILL);
        lyricPaint.setTextSize(50);
        lyricPaint.setTextAlign(Paint.Align.CENTER);
        // 中线歌词笔触
        lyricSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lyricSelectPaint.setColor(getResources().getColor(R.color.colorlyricSelect));
        lyricSelectPaint.setStyle(Paint.Style.FILL);
        lyricSelectPaint.setTextSize(50);
        lyricSelectPaint.setTextAlign(Paint.Align.CENTER);
        // 中线笔触
        centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerLinePaint.setColor(getResources().getColor(R.color.colorCenterLine));
        centerLinePaint.setStyle(Paint.Style.FILL);
        lyricSelectPaint.setTextSize(50);
        // 曲谱笔触
        turnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        turnPaint.setColor(getResources().getColor(R.color.colorlyric));
        turnPaint.setStyle(Paint.Style.FILL);
        turnPaint.setTextSize(50);
        turnPaint.setTextAlign(Paint.Align.CENTER);
        // 中线曲谱笔触
        turnSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        turnSelectPaint.setColor(getResources().getColor(R.color.colorScoreSelect));
        turnSelectPaint.setStyle(Paint.Style.FILL);
        turnSelectPaint.setTextAlign(Paint.Align.CENTER);
        turnSelectPaint.setTextSize(50);

        // 演奏过笔触
        playedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        playedPaint.setColor(getResources().getColor(R.color.colorPlayed));
        playedPaint.setStyle(Paint.Style.FILL);
        playedPaint.setTextAlign(Paint.Align.CENTER);
        playedPaint.setTextSize(50);

        gestureDetector = new GestureDetector(getContext(), new MyGestureDetector());

        setBackgroundColor(getResources().getColor(R.color.colorlyricsViewBackground));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        itemHeight = viewHeight / 5;
        centerLineY = viewHeight / 2;

        Paint.FontMetricsInt lyricFm = lyricPaint.getFontMetricsInt();

        int line = 0;
        int x = 0;
        for (int i = 0; i < lyrics.size(); i++) {

            if (i == 0) {
                line = 0;
            }

            if (i % 13 == 0 && i != 0) {
                line++;
            }

            x = i % 13 * viewWidth / 13 + viewWidth / 13 / 2;
            lyrics.get(i).setBaseline(line * itemHeight + itemHeight / 2 - (lyricFm.bottom - lyricFm.top) / 2 - lyricFm.top);
            lyrics.get(i).setX(x);
            lyrics.get(i).setLineNum(line);
            lyricsCopy.get(i).setBaseline(line * itemHeight + itemHeight / 2 - (lyricFm.bottom - lyricFm.top) / 2 - lyricFm.top);
            lyricsCopy.get(i).setX(x);
            lyricsCopy.get(i).setLineNum(line);
        }

        inViewHeight = itemHeight * (line + 1);

        Paint.FontMetricsInt scoreFm = turnPaint.getFontMetricsInt();

        line = 0;
        x = 0;
        for (int i = 0; i < tunes.size(); i++) {
            if (i == 0) {
                line = 0;
            }

            if (i % 13 == 0 && i != 0) {
                line++;
            }

            x = i % 13 * viewWidth / 13 + viewWidth / 13 / 2;
            tunes.get(i).setBaseline(line * itemHeight + itemHeight / 2 - (scoreFm.bottom - scoreFm.top) / 2);
            tunes.get(i).setX(x);
            tunesCopy.get(i).setBaseline(line * itemHeight + itemHeight / 2 - (scoreFm.bottom - scoreFm.top) / 2);
            tunesCopy.get(i).setX(x);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画中间线
        canvas.drawLine(0, centerLineY, viewWidth, centerLineY, centerLinePaint);

        // 画歌词
        for (int i = 0; i < lyrics.size(); i++) {
            Lyric lyric = lyrics.get(i);
            if ((lyric.getBaseline() < centerLineY + itemHeight / 2) && (lyric.getBaseline() > centerLineY - itemHeight / 2)) {
                canvas.drawText(lyric.getLyric() + "", lyric.getX(), lyric.getBaseline(), lyricSelectPaint);
            } else {
                canvas.drawText(lyric.getLyric() + "", lyric.getX(), lyric.getBaseline(), lyricPaint);
            }

            if (lyric.isPlayed()) {
                canvas.drawText(lyric.getLyric() + "", lyric.getX(), lyric.getBaseline(), playedPaint);
            }
        }
        // 画谱
        for (int i = 0; i < tunes.size(); i++) {
            Tune tune = tunes.get(i);
            if ((tune.getBaseline() < centerLineY + itemHeight / 2) && (tune.getBaseline() > centerLineY - itemHeight / 2)) {
                canvas.drawText(tune.getTune() + "", tune.getX(), tune.getBaseline(), lyricSelectPaint);
            } else {
                canvas.drawText(tune.getTune() + "", tune.getX(), tune.getBaseline(), lyricPaint);
            }

            if (tune.isPlayed()) {
                canvas.drawText(tune.getTune() + "", tune.getX(), tune.getBaseline(), playedPaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                resetLines();
                break;
        }


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

            for (int i = 0; i < lyrics.size(); i++) {
                lyrics.get(i).setBaseline(lyricsCopy.get(i).getBaseline() - scrllY);
            }

            for (int i = 0; i < tunes.size(); i++) {
                tunes.get(i).setBaseline(tunesCopy.get(i).getBaseline() - scrllY);
            }

            invalidate();
            return;
        }

        if (scrllY >= inViewHeight - viewHeight / 2) {
            scrllY = inViewHeight - viewHeight / 2;

            for (int i = 0; i < lyrics.size(); i++) {
                lyrics.get(i).setBaseline(lyricsCopy.get(i).getBaseline() - scrllY);
            }


            for (int i = 0; i < tunes.size(); i++) {
                tunes.get(i).setBaseline(tunesCopy.get(i).getBaseline() - scrllY);
            }

            invalidate();
            return;
        }

        for (int i = 0; i < lyrics.size(); i++) {
            lyrics.get(i).setBaseline(lyrics.get(i).getBaseline() - distanceY);
        }

        for (int i = 0; i < tunes.size(); i++) {
            tunes.get(i).setBaseline(tunes.get(i).getBaseline() - distanceY);
        }

        invalidate();
    }

    public void scrollTo(final int startY, int endY) {

        final ValueAnimator valueAnimator = ValueAnimator.ofInt(startY, endY);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int start = startY;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                int distance = value - start;
                scroll(distance);
                start = value;
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isScrolling = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isScrolling = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(itemHeight);
        valueAnimator.start();
    }

    public void play() {

        if (playNum > lyrics.size() - 1) {
            return;
        }

        for (int i = 0; i < lyrics.size(); i++) {
            if (i == playNum) {
                lyrics.get(i).setPlayed(true);
            }
        }

        for (int i = 0; i < tunes.size(); i++) {
            if (i == playNum) {
                tunes.get(i).setPlayed(true);
            }
        }

        // 播放相应的声音

//        playSound(tunes.get(playNum).getTune() + "");

        int i = lyrics.get(playNum).getBaseline() - centerLineY >= 0 ?
                (lyrics.get(playNum).getBaseline() - centerLineY + itemHeight / 2) / itemHeight
                : (lyrics.get(playNum).getBaseline() - centerLineY - itemHeight / 2) / itemHeight;
//
//        Log.d("123", "lyrics.get(playNum).getBaseline() = " + lyrics.get(playNum).getBaseline());
//        Log.d("123", "i = " + i);
//        Log.d("123", "");

        int distance = i * itemHeight;

        if (distance == 0) {
            invalidate();
        } else {
            if (!isScrolling) {
                Log.d("123", "滚动");
                scrollTo(0, distance);
            }
        }

        playNum++;
    }

    public void resetLines() {

        if (scrllY == 0 || scrllY == inViewHeight - viewHeight / 2) {
            return;
        }

        int distance = scrllY % itemHeight;

        if (distance < itemHeight / 2) {
            scrollTo(0, -distance);
        }

        if (distance > itemHeight / 2) {
            scrollTo(0, itemHeight - distance);
        }
    }

    public void playSound(String note) {

        String uriStr = "android.resource://" + getContext().getPackageName() + "/";
        Uri uri = null;
        switch (note) {
            case "1":
                uri = Uri.parse(uriStr + R.raw.a1m);
                break;
            case "2":
                uri = Uri.parse(uriStr + R.raw.a2m);
                break;
            case "3":
                uri = Uri.parse(uriStr + R.raw.a3m);
                break;
            case "4":
                uri = Uri.parse(uriStr + R.raw.a4m);
                break;
            case "5":
                uri = Uri.parse(uriStr + R.raw.a5m);
                break;
            case "6":
                uri = Uri.parse(uriStr + R.raw.a6m);
                break;
            case "7":
                uri = Uri.parse(uriStr + R.raw.a7m);
                break;
            default:
                uri = Uri.parse(uriStr + R.raw.a1m);
                break;

        }

        final MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();//重置为初始状态
        }
        try {
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.prepare();//缓冲
            mediaPlayer.start();//开始或恢复播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播出完毕事件
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {// 错误处理事件
                @Override
                public boolean onError(MediaPlayer player, int arg1, int arg2) {
                    mediaPlayer.release();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer.release();//释放资源
        }

    }
}
