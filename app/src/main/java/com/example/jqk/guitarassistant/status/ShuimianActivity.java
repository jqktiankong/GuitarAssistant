package com.example.jqk.guitarassistant.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jqk.guitarassistant.R;

public class ShuimianActivity extends AppCompatActivity {
    private ImageView back;
    private FrameLayout contentView;
    private boolean touch;
    private int heigth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuimian);

        contentView = findViewById(R.id.contentView);
        back = findViewById(R.id.back);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        heigth = dm.heightPixels;
        Log.d("123", "heigth = " + heigth);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("123", "v.getY() = " + event.getY(0));
                        if (event.getY(0) >= heigth / 3 * 2) {
                            touch = true;
                        } else {
                            touch = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (touch) {
                            Intent intent = new Intent();
                            intent.setClass(ShuimianActivity.this, ChenmaiActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
    }
}
