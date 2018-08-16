package com.example.jqk.guitarassistant.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jqk.guitarassistant.R;

public class StatusFragment extends Fragment implements View.OnClickListener {
    private LinearLayout xinlv, shuimian, duanlian;
    private ImageView header;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        header = view.findViewById(R.id.header);
        xinlv = view.findViewById(R.id.xinlvView);
        shuimian = view.findViewById(R.id.shuimianView);
        duanlian = view.findViewById(R.id.duanlianView);

        xinlv.setOnClickListener(this);
        header.setOnClickListener(this);
        shuimian.setOnClickListener(this);
        duanlian.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.header:
                intent.setClass(getActivity(), HeaderActivity.class);
                startActivity(intent);
                break;
            case R.id.xinlvView:
                intent.setClass(getActivity(), XinlvActivity.class);
                startActivity(intent);
                break;
            case R.id.shuimianView:
                intent.setClass(getActivity(), ShuimianActivity.class);
                startActivity(intent);
                break;
            case R.id.duanlianView:
                intent.setClass(getActivity(), DuanlianActivity.class);
                startActivity(intent);
                break;
        }
    }
}
