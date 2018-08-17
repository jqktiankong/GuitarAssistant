package com.example.jqk.guitarassistant.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jqk.guitarassistant.R;

public class MineFragment extends Fragment implements View.OnClickListener {

    private ImageView user;
    private RelativeLayout wodemubiaoView, wodeshebeiView, disanfangView,
            yijianfankuiView, shiyongbangzhuView, shezhiView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        init(view);

        return view;
    }

    public void init(View view) {
        user = view.findViewById(R.id.user);
        wodemubiaoView = view.findViewById(R.id.wodemubiaoView);
        wodeshebeiView = view.findViewById(R.id.wodeshebeiView);
        disanfangView = view.findViewById(R.id.disanfangView);
        yijianfankuiView = view.findViewById(R.id.yijianfankuiView);
        shiyongbangzhuView = view.findViewById(R.id.shiyongbangzhuView);
        shezhiView = view.findViewById(R.id.shezhiView);

        user.setOnClickListener(this);
        wodemubiaoView.setOnClickListener(this);
        wodeshebeiView.setOnClickListener(this);
        disanfangView.setOnClickListener(this);
        yijianfankuiView.setOnClickListener(this);
        shiyongbangzhuView.setOnClickListener(this);
        shezhiView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.user:
                intent.setClass(getContext(), UserActivity.class);
                startActivity(intent);
                break;
            case R.id.wodemubiaoView:
                intent.setClass(getContext(), WodemubiaoActivity.class);
                startActivity(intent);
                break;
            case R.id.wodeshebeiView:
                intent.setClass(getContext(), WodeshebeiActivity.class);
                startActivity(intent);
                break;
            case R.id.disanfangView:
                intent.setClass(getContext(), DisanfangActivity.class);
                startActivity(intent);
                break;
            case R.id.yijianfankuiView:
                intent.setClass(getContext(), YijianfankuiActivity.class);
                startActivity(intent);
                break;
            case R.id.shiyongbangzhuView:
                intent.setClass(getContext(), ShiyongbangzhuActivity.class);
                startActivity(intent);
                break;
            case R.id.shezhiView:
                intent.setClass(getContext(), ShezhiActivity.class);
                startActivity(intent);
                break;
        }
    }
}
