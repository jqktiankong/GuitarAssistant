package com.example.jqk.guitarassistant.free;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;

import java.io.IOException;

public class FreeActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener {

    private ImageView img;
    private ImageView guitarImg;
    private RelativeLayout guitarParentView;
    private LinearLayout imgParentView;
    private Button imgd, imga, imgf, imgg, imgc, imge;
    private TextView title;
    private Button play;
    private SeekBar seekBar;
    // 调节音量
    private AudioManager mAudioManager;
    // 系统最大音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;

    private String tune = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);

        init();
    }

    public void init() {
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);
        guitarImg = findViewById(R.id.guitarImg);
        guitarParentView = findViewById(R.id.guitarParentView);
        imgParentView = findViewById(R.id.imgParentView);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekBar);

        imgd = findViewById(R.id.imgd);
        imga = findViewById(R.id.imga);
        imgf = findViewById(R.id.imgf);
        imgg = findViewById(R.id.imgg);
        imgc = findViewById(R.id.imgc);
        imge = findViewById(R.id.imge);

        title.setText("自由模式");

        imgd.setOnTouchListener(this);
        imga.setOnTouchListener(this);
        imgf.setOnTouchListener(this);
        imgg.setOnTouchListener(this);
        imgc.setOnTouchListener(this);
        imge.setOnTouchListener(this);

        play.setOnClickListener(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 设置当前音量显示
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int parentHeight = imgParentView.getMeasuredHeight();
            float imgHeight = parentHeight / 5.0f * 4.0f;
            float imgWidth = imgHeight / 410.0f * 320.0f;
            ViewGroup.LayoutParams lp = img.getLayoutParams();
            lp.width = (int) imgWidth;
            lp.height = (int) imgHeight;
            img.setLayoutParams(lp);

            Log.d("123", "imgHeight = " + (int) imgHeight);
            Log.d("123", "imgWidth = " + (int) imgWidth);

            int guitarParentHeight = guitarParentView.getMeasuredHeight();
            int guitarWidth = guitarParentView.getMeasuredWidth();
            ViewGroup.LayoutParams lp1 = guitarImg.getLayoutParams();
            lp1.width = guitarWidth;
            lp1.height = guitarParentHeight;
            guitarImg.setLayoutParams(lp1);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.imgd:
                        tune = "d";
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_dd);
                        break;
                    case R.id.imga:
                        tune = "a";
                        imgd.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_aa);
                        break;
                    case R.id.imgf:
                        tune = "f";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_ff);
                        break;
                    case R.id.imgg:
                        tune = "g";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_gg);
                        break;
                    case R.id.imgc:
                        tune = "c";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_cc);
                        break;
                    case R.id.imge:
                        tune = "e";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_ee);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                tune = "";
                imgd.setOnTouchListener(this);
                imga.setOnTouchListener(this);
                imgf.setOnTouchListener(this);
                imgg.setOnTouchListener(this);
                imgc.setOnTouchListener(this);
                imge.setOnTouchListener(this);

                img.setImageResource(R.drawable.icon_00);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
//                playSound(tune);
                break;
        }
    }

    public void playSound(String note) {
        String uriStr = "android.resource://" + getPackageName() + "/";
        Uri uri = null;
        switch (note) {
            case "d":
                uri = Uri.parse(uriStr + R.raw.dd);
                break;
            case "a":
                uri = Uri.parse(uriStr + R.raw.aa);
                break;
            case "f":
                uri = Uri.parse(uriStr + R.raw.ff);
                break;
            case "g":
                uri = Uri.parse(uriStr + R.raw.gg);
                break;
            case "c":
                uri = Uri.parse(uriStr + R.raw.cc);
                break;
            case "e":
                uri = Uri.parse(uriStr + R.raw.ee);
                break;
            case "":
                uri = Uri.parse(uriStr + R.raw.empty);
                break;
        }

        final MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();//重置为初始状态
        }
        try {
            mediaPlayer.setDataSource(this, uri);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentVolume = progress;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                currentVolume--;
                currentVolume = currentVolume <= 0 ? 0 : currentVolume;
                currentVolume = currentVolume >= maxVolume ? maxVolume : currentVolume;
                setVolume(currentVolume);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                currentVolume++;
                currentVolume = currentVolume <= 0 ? 0 : currentVolume;
                currentVolume = currentVolume >= maxVolume ? maxVolume : currentVolume;
                setVolume(currentVolume);
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                Log.d("123", "KEYCODE_VOLUME_MUTE");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setVolume(int volume) {
        seekBar.setProgress(volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }
}
