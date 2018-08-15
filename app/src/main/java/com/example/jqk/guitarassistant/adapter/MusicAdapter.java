package com.example.jqk.guitarassistant.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.lyric.LyricActivity;
import com.example.jqk.guitarassistant.music.Song;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Activity context;
    private List<Song> songs;

    public MusicAdapter(Activity context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initials.setText(songs.get(i).getInitials());
        viewHolder.songName.setText(songs.get(i).getSongName());
        viewHolder.name.setText(songs.get(i).getName());

        if (songs.get(i).getStar() == 1) {
            viewHolder.star.setImageResource(R.drawable.icon_star1);
        } else {
            viewHolder.star.setImageResource(R.drawable.icon_star2);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, LyricActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout itemView;
        private ImageView star;
        private TextView songName, name, initials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView.findViewById(R.id.itemView);
            initials = itemView.findViewById(R.id.initials);
            star = itemView.findViewById(R.id.star);
            songName = itemView.findViewById(R.id.songName);
            name = itemView.findViewById(R.id.name);
        }
    }
}
