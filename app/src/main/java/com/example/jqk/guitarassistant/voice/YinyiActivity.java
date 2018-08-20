package com.example.jqk.guitarassistant.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;

public class YinyiActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back, setting;
    private TextView title;
    private ImageView input, start;
    private LinearLayout bottomView;

    private int type = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinyi);
        init();
        title.setText("音译模式");

        back.setOnClickListener(this);
        setting.setOnClickListener(this);
        input.setOnClickListener(this);
        start.setOnClickListener(this);
    }

    public void init() {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        setting = findViewById(R.id.setting);
        input = findViewById(R.id.input);
        start = findViewById(R.id.start);
        bottomView = findViewById(R.id.bottomView);
    }

    public void setType(int t) {
        if (t == 1) {
            bottomView.setBackgroundResource(R.drawable.icon_yinyi_bottom2);
            start.setImageResource(R.drawable.icon_yuyin2);

            type = 2;
        } else {
            bottomView.setBackgroundResource(R.drawable.icon_yinyi_bottom1);
            start.setImageResource(R.drawable.icon_yuyin1);

            type = 1;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting:
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.input:
                intent.setClass(this, InputActivity.class);
                startActivity(intent);
                break;
            case R.id.start:
                setType(type);
                break;
        }
    }
}
