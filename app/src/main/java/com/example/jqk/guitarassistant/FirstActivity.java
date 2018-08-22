package com.example.jqk.guitarassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.jqk.guitarassistant.util.SPUtils;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();

        if ((boolean) SPUtils.get(this, "n", false)) {
            intent.setClass(this, StartActivity.class);
        } else {
            intent.setClass(this, NavigationActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
