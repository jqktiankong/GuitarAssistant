package com.example.jqk.guitarassistant.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.adapter.FlexAdatper;
import com.example.jqk.guitarassistant.free.FreeActivity;
import com.example.jqk.guitarassistant.search.SearchActivty;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class MusicFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private String[] songs;
    private ImageView music, free;
    private RelativeLayout searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        init(view);
        return view;
    }

    public void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        music = view.findViewById(R.id.music);
        free = view.findViewById(R.id.free);
        searchView = view.findViewById(R.id.searchView);

        music.setOnClickListener(this);
        free.setOnClickListener(this);
        searchView.setOnClickListener(this);

        songs = getResources().getStringArray(R.array.songs);

        FlexAdatper flexAdatper = new FlexAdatper(songs);
        recyclerView.setAdapter(flexAdatper);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.music:
                intent.setClass(getActivity(), MusicActivity.class);
                startActivity(intent);
                break;
            case R.id.free:
                intent.setClass(getActivity(), FreeActivity.class);
                startActivity(intent);
                break;
            case R.id.searchView:
                intent.setClass(getActivity(), SearchActivty.class);
                startActivity(intent);
                break;
        }
    }
}
