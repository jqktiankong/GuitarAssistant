package com.example.jqk.guitarassistant;

import android.app.UiAutomation;
import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView title;
    private LyricsView lyricsView;
    private Button play;
    private SeekBar seekBar;

    private String geci;
    private String jianpu;
    // 调节音量
    private AudioManager mAudioManager;
    // 系统最大音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        title.setText("成都");
        play.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        // 获取歌词简谱
        geci = getResources().getString(R.string.geci1);
        jianpu = getResources().getString(R.string.jianpu1);
        // 设置歌词，简谱
        lyricsView.setLyrics(Utils.lyricTransform(geci));
        lyricsView.setTunes(Utils.lyricTransform(jianpu));

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 设置当前音量显示
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
    }

    public void init() {
        lyricsView = findViewById(R.id.lyricsView);
        title = findViewById(R.id.title);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekBar);
    }

    public void setVolume(int volume) {
        seekBar.setProgress(volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                lyricsView.play();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
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
}
