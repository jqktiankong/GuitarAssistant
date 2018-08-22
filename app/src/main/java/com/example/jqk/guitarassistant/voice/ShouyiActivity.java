package com.example.jqk.guitarassistant.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;

public class ShouyiActivity extends AppCompatActivity implements View.OnClickListener {

    private int type = 2;
    private RelativeLayout contentView;
    private Button button;
    private ImageView back, setting;
    private TextView title;
    private ImageView input, start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shouyi);
        init();

        title.setText("手译模式");

        back.setOnClickListener(this);
        button.setOnClickListener(this);
        setting.setOnClickListener(this);

        setType(type);
    }

    public void init() {
        contentView = findViewById(R.id.contentView);
        button = findViewById(R.id.button);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        setting = findViewById(R.id.setting);
        input = findViewById(R.id.input);
        start = findViewById(R.id.start);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button:
                if (type == 1) {
                    setType(type);
                }
                break;
            case R.id.setting:
                Intent intent = new Intent();
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void setType(int t) {
        if (t == 1) {
            contentView.setBackgroundResource(R.drawable.icon_shouyi_bg2);
            button.setBackgroundResource(R.drawable.bg_shouyi2);
            button.setText("停止翻译");

            type = 2;
        } else {
            contentView.setBackgroundResource(R.drawable.icon_shouyi_bg1);
            button.setBackgroundResource(R.drawable.bg_shouyi1);
            button.setText("开始翻译");

            type = 1;
        }
    }
}
