package com.example.jqk.guitarassistant.voice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jqk.guitarassistant.R;

public class VoiceFragment extends Fragment implements View.OnClickListener {

    private ImageView shouyimoshi, yinyimoshi, daibodianhua;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice, container, false);
        init(view);

        shouyimoshi.setOnClickListener(this);
        yinyimoshi.setOnClickListener(this);
        daibodianhua.setOnClickListener(this);
        return view;
    }

    public void init(View view) {
        shouyimoshi = view.findViewById(R.id.shouyimoshi);
        yinyimoshi = view.findViewById(R.id.yinyimoshi);
        daibodianhua = view.findViewById(R.id.daibodianhua);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.shouyimoshi:
                intent.setClass(getContext(), ShouyiActivity.class);
                startActivity(intent);
                break;
            case R.id.yinyimoshi:
                intent.setClass(getContext(), YinyiActivity.class);
                startActivity(intent);
                break;
            case R.id.daibodianhua:
                intent.setClass(getContext(), PhoneActivity.class);
                startActivity(intent);
                break;
        }
    }
}
