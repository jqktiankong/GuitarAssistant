package com.example.jqk.guitarassistant.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jqk.guitarassistant.ProgerssDialog;
import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.adapter.FlexSearchAdatper;
import com.example.jqk.guitarassistant.adapter.SearchResultAdapter;
import com.example.jqk.guitarassistant.music.Song;
import com.example.jqk.guitarassistant.util.DBManager;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

public class SearchActivty extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final int SUCCESS = 1000;
    private static final int FAIL = 1001;

    private ImageView back;
    private EditText search;
    private TextView prompt;
    private RecyclerView recyclerView;
    private DBManager dbManager;
    private List<Song> songs;
    private ProgerssDialog progerssDialog;
    private FragmentTransaction ft;

    private String[] songList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    hideProgress();
                    prompt.setText("搜索结果");
                    songs = new ArrayList<>();
                    Song song = new Song();
                    song.setSongName(search.getText().toString().trim());
                    songs.add(song);
                    SearchResultAdapter searchResultAdapter = new SearchResultAdapter(SearchActivty.this, songs);
                    recyclerView.setAdapter(searchResultAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivty.this));
                    break;
                case FAIL:
                    hideProgress();
                    Toast.makeText(SearchActivty.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
    }

    public void init() {
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        prompt = findViewById(R.id.prompt);

        back.setOnClickListener(this);
        search.setOnEditorActionListener(this);

        dbManager = new DBManager(this);
        songs = dbManager.querySongList();
        FlexSearchAdatper flexAdatper = new FlexSearchAdatper(songs);
        recyclerView.setAdapter(flexAdatper);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);

        prompt.setText("最近搜索");
        songList = getResources().getStringArray(R.array.songs);
    }

    public void showProgress() {
        if (progerssDialog == null) {
            progerssDialog = new ProgerssDialog();
            ft = getSupportFragmentManager().beginTransaction();
            ft.add(progerssDialog, "progerssDialog");
            if (!progerssDialog.isAdded() && !progerssDialog.isVisible() && !progerssDialog.isRemoving()) {
                ft.commitAllowingStateLoss();
            }
        } else {
            if (!progerssDialog.isAdded() && !progerssDialog.isVisible() && !progerssDialog.isRemoving()) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.add(progerssDialog, "progerssDialog");
                ft.commitAllowingStateLoss();
            }
        }
    }

    public void hideProgress() {
        if (progerssDialog != null) {
            progerssDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String s = search.getText().toString().trim();
            showProgress();
            boolean result = false;
            for (String str : songList) {
                if (str.equals(s)) {
                    result = true;
                }
            }
            if (result) {
                if (!dbManager.querySongListResult(s)) {
                    Song song = new Song();
                    song.setId(System.currentTimeMillis());
                    song.setSongName(search.getText().toString().trim());
                    dbManager.insertSong(song);
                }
                handler.sendEmptyMessageDelayed(SUCCESS, 1000);
            } else {
                handler.sendEmptyMessageDelayed(FAIL, 1000);
            }
            return true;
        }
        return false;
    }
}
