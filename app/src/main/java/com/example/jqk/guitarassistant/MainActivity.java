package com.example.jqk.guitarassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jqk.guitarassistant.music.MusicFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout musicView, voiceView, statusView, mineView;
    private ImageView music, voice, status, mine;
    private TextView musicTv, voiceTv, statusTv, mineTv;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MusicFragment musicFragment;
    private VoiceFragment voiceFragment;
    private StatusFragment statusFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            musicFragment = (MusicFragment) fragmentManager.findFragmentByTag("musicFragment");
            voiceFragment = (VoiceFragment) fragmentManager.findFragmentByTag("voiceFragment");
            statusFragment = (StatusFragment) fragmentManager.findFragmentByTag("statusFragment");
            mineFragment = (MineFragment) fragmentManager.findFragmentByTag("mineFragment");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        showFragment(3);
    }

    public void init() {
        musicView = findViewById(R.id.musicView);
        voiceView = findViewById(R.id.voiceView);
        statusView = findViewById(R.id.statusView);
        mineView = findViewById(R.id.mineView);

        music = findViewById(R.id.music);
        voice = findViewById(R.id.voice);
        status = findViewById(R.id.status);
        mine = findViewById(R.id.mine);

        musicTv = findViewById(R.id.musicTv);
        voiceTv = findViewById(R.id.voiceTv);
        statusTv = findViewById(R.id.statusTv);
        mineTv = findViewById(R.id.mineTv);

        musicView.setOnClickListener(this);
        voiceView.setOnClickListener(this);
        statusView.setOnClickListener(this);
        mineView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statusView:
                showFragment(1);
                break;
            case R.id.voiceView:
                showFragment(2);
                break;
            case R.id.musicView:
                showFragment(3);
                break;
            case R.id.mineView:
                showFragment(4);
                break;
        }
    }

    public void showFragment(int type) {
        switch (type) {
            case 1:
                fragmentTransaction = fragmentManager.beginTransaction();
                if (statusFragment == null) {
                    statusFragment = new StatusFragment();
                }
                fragmentTransaction.replace(R.id.fragmentView, statusFragment, "statusFragment");
                fragmentTransaction.commit();

                status.setImageResource(R.drawable.icon_status_select);
                voice.setImageResource(R.drawable.icon_voice);
                music.setImageResource(R.drawable.icon_music);
                mine.setImageResource(R.drawable.icon_mine);

                statusTv.setTextColor(getResources().getColor(R.color.searchTextColor));
                voiceTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                musicTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                mineTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                break;
            case 2:
                fragmentTransaction = fragmentManager.beginTransaction();
                if (voiceFragment == null) {
                    voiceFragment = new VoiceFragment();
                }
                fragmentTransaction.replace(R.id.fragmentView, voiceFragment, "voiceFragment");
                fragmentTransaction.commit();

                status.setImageResource(R.drawable.icon_status);
                voice.setImageResource(R.drawable.icon_voice_select);
                music.setImageResource(R.drawable.icon_music);
                mine.setImageResource(R.drawable.icon_mine);

                statusTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                voiceTv.setTextColor(getResources().getColor(R.color.searchTextColor));
                musicTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                mineTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                break;
            case 3:
                fragmentTransaction = fragmentManager.beginTransaction();
                if (musicFragment == null) {
                    musicFragment = new MusicFragment();
                }
                fragmentTransaction.replace(R.id.fragmentView, musicFragment, "musicFragment");
                fragmentTransaction.commit();

                status.setImageResource(R.drawable.icon_status);
                voice.setImageResource(R.drawable.icon_voice);
                music.setImageResource(R.drawable.icon_music_select);
                mine.setImageResource(R.drawable.icon_mine);

                statusTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                voiceTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                musicTv.setTextColor(getResources().getColor(R.color.searchTextColor));
                mineTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                break;
            case 4:
                fragmentTransaction = fragmentManager.beginTransaction();
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                }
                fragmentTransaction.replace(R.id.fragmentView, mineFragment, "mineFragment");
                fragmentTransaction.commit();

                status.setImageResource(R.drawable.icon_status);
                voice.setImageResource(R.drawable.icon_voice);
                music.setImageResource(R.drawable.icon_music);
                mine.setImageResource(R.drawable.icon_mine_select);

                statusTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                voiceTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                musicTv.setTextColor(getResources().getColor(R.color.defaultTvColor));
                mineTv.setTextColor(getResources().getColor(R.color.searchTextColor));
                break;
        }
    }
}
