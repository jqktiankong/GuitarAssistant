package com.example.jqk.guitarassistant.voice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jqk.guitarassistant.R;

public class PhoneActivity1 extends AppCompatActivity {

    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone1);

        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        title.setText("电话内容");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
