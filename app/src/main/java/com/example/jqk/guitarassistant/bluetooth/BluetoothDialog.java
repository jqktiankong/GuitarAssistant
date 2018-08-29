package com.example.jqk.guitarassistant.bluetooth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jqk.guitarassistant.R;

public class BluetoothDialog extends DialogFragment {

    private Button start;
    private RippleView rippleView;
    private OnBluetoothClickListener onBluetoothClickListener;
    public interface OnBluetoothClickListener {
        void start();
    }

    public void setOnBluetoothClickListener(OnBluetoothClickListener onBluetoothClickListener) {
        this.onBluetoothClickListener = onBluetoothClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bluetooth, container, false);
        start = view.findViewById(R.id.start);
        rippleView = view.findViewById(R.id.rippleView);
        rippleView.setCircleNumber(5);
        rippleView.setColor("#459021");
        rippleView.setNewCircleTime(1000);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRipple();
                onBluetoothClickListener.start();
            }
        });
        return view;
    }

    public void startRipple() {
        if (!rippleView.isStarting()) {
            rippleView.start();
            setShowText("正在搜索设备");
        }
    }

    public void setShowText(String str) {
        start.setText(str);
    }
}
