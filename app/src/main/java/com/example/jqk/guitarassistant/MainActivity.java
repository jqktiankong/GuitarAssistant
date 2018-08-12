package com.example.jqk.guitarassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private LyricsView lyricsView;
    private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lyricsView = findViewById(R.id.lyricsView);
        title = findViewById(R.id.title);
        play = findViewById(R.id.play);

        title.setText("成都");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyricsView.play();
            }
        });
    }
}
