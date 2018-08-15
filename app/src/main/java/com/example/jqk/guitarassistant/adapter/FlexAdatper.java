package com.example.jqk.guitarassistant.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;
import com.google.android.flexbox.FlexboxLayoutManager;

public class FlexAdatper extends RecyclerView.Adapter<FlexAdatper.ViewHolder> {

    private String[] songs;

    public FlexAdatper(String[] songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_flex, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ViewGroup.LayoutParams lp = viewHolder.linearLayout.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexGrow(1.0f);
        }

        viewHolder.tv.setText(songs[i]);
    }

    @Override
    public int getItemCount() {
        return songs.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.text);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
