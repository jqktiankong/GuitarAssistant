package com.example.jqk.guitarassistant.music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.jqk.guitarassistant.util.ChineseCharacterHelper;
import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.adapter.MusicAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    private ImageView back;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<Song> datas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void init() {
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView);

        String[] songs = getResources().getStringArray(R.array.songs);
        String[] names = getResources().getStringArray(R.array.names);
        String[] stars = getResources().getStringArray(R.array.stars);

        datas = new ArrayList<Song>();
        for (int i = 0; i < songs.length; i++) {
            Song song = new Song();
            song.setInitials((ChineseCharacterHelper.getFirstLetter(songs[i].toCharArray()[0]) + "").toUpperCase());
            song.setSongName(songs[i]);
            song.setName(names[i]);
            song.setStar(Integer.parseInt(stars[i]));

            datas.add(song);
        }
        musicAdapter = new MusicAdapter(this, datas);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
