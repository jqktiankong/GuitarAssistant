package com.example.jqk.guitarassistant.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;

public class PhoneActivity extends AppCompatActivity {

    private TextView title;
    private ImageView back, select;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        select = findViewById(R.id.select);

        title.setText("场景选择");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhoneActivity.this, PhoneActivity1.class);
                startActivity(intent);
            }
        });
    }
}
