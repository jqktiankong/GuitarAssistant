package com.example.jqk.guitarassistant.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShiyongbangzhuActivity extends AppCompatActivity {
    private ImageView back;
    private ViewPager viewPager;
    private List<View> viewList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shiyongbangzhu);

        viewPager = findViewById(R.id.viewPager);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewList = new ArrayList<>();
        ImageView img1 = new ImageView(this);
        img1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        img1.setScaleType(ImageView.ScaleType.FIT_XY);
        img1.setImageResource(R.drawable.icon_shiyongbangzhu1);
        viewList.add(img1);

        ImageView img2 = new ImageView(this);
        img2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        img2.setScaleType(ImageView.ScaleType.FIT_XY);
        img2.setImageResource(R.drawable.icon_shiyongbangzhu2);
        viewList.add(img2);

        ImageView img3 = new ImageView(this);
        img3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        img3.setScaleType(ImageView.ScaleType.FIT_XY);
        img3.setImageResource(R.drawable.icon_shiyongbangzhu3);
        viewList.add(img3);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
