package com.example.jqk.guitarassistant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> lyrics;
    private ArrayList<String> Score;

    public LyricsAdapter(Context context, ArrayList<String> lyrics, ArrayList<String> score) {
        this.context = context;
        this.lyrics = lyrics;
        Score = score;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
