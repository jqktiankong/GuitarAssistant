package com.example.jqk.guitarassistant.lyric;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.bluetooth.BluetoothDialog;
import com.example.jqk.guitarassistant.bluetooth.BluetoothService;
import com.example.jqk.guitarassistant.event.MessageEvent;
import com.example.jqk.guitarassistant.util.Constants;
import com.example.jqk.guitarassistant.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

import static com.example.jqk.guitarassistant.util.Constants.REQUEST_ENABLE_BT;

public class LyricActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView title;
    private LyricsView lyricsView;
    private SeekBar seekBar;
    private ImageView left, right, reset, back;

    private String geci;
    private String jianpu;
    // 调节音量
    private AudioManager mAudioManager;
    // 系统最大音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;

    private int content = 0;
    private int songsSize = 0;
    // 蓝牙
    private FragmentTransaction ft;
    private BluetoothDialog bluetoothDialog;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothService bluetoothService;

    private long timeNow, timeLast;

    private boolean isConnected = false;
    private boolean isfonded = false;

    // 蓝牙接受广播
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int state;
            // When discovery finds a device
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            // 蓝牙开启
                            showDialog();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            // 蓝牙关闭
                            hideDialog();
                            Toast.makeText(LyricActivity.this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
                            if (bluetoothService != null) {
                                bluetoothService.stop();
                            }
                            finish();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("123", "device.getAddress() = " + bluetoothDevice.getAddress());

                    foundBT(bluetoothDevice);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (isfonded && !isConnected) {
                        bluetoothAdapter.startDiscovery();
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    switch (state) {
                        case BluetoothDevice.BOND_NONE:
                            Log.d("123", "删除配对");
                            Toast.makeText(LyricActivity.this, "配对失败", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("123", "正在配对");
                            setType("正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("123", "配对成功");
                            Toast.makeText(LyricActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                            foundBT(bluetoothDevice);
                            break;
                        default:
                            Toast.makeText(LyricActivity.this, "未知原因", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                    break;
            }
        }
    };

    // 注册注销eventBus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // 接收eventBus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getMark()) {
            case Constants.MESSAGE_STATE_CHANGE:
                switch (event.getState()) {
                    case BluetoothService.STATE_NONE:

                        break;
                    case BluetoothService.STATE_LISTEN:

                        break;
                    case BluetoothService.STATE_CONNECTING:
                        setType("正在连接设备");
                        break;
                    case BluetoothService.STATE_CONNECTED:
                        bluetoothAdapter.cancelDiscovery();
                        setType("设备连接成功");
                        Toast.makeText(LyricActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        isConnected = true;
                        hideDialog();
                        break;
                }
                break;
            case Constants.MESSAGE_PLAY:
                timeNow = System.currentTimeMillis();
                if (timeNow - timeLast > 500) {
                    timeLast = timeNow;
                    lyricsView.play();
                }
                break;
            case Constants.MESSAGE_TOAST:
                Toast.makeText(LyricActivity.this, event.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);

        init();

        left.setOnClickListener(this);
        reset.setOnClickListener(this);
        right.setOnClickListener(this);
        back.setOnClickListener(this);

        songsSize = getResources().getStringArray(R.array.songs).length;
        // 获取歌词简谱
        content = getIntent().getIntExtra("content", 0);
        // 避免数组越界
        if (content > getResources().getStringArray(R.array.geci).length - 1) {
            content = 0;
        }

        title.setText(getResources().getStringArray(R.array.songs)[content]);

        geci = getResources().getStringArray(R.array.geci)[content];
        jianpu = getResources().getStringArray(R.array.jianpu)[content];
        // 设置歌词，简谱
        lyricsView.setLyrics(Utils.lyricTransform(geci));
        lyricsView.setTunes(Utils.lyricTransform(jianpu));

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 设置当前音量显示
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
        seekBar.setOnSeekBarChangeListener(this);

        // 蓝牙设置
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            showDialog();
        }

        IntentFilter filter = new IntentFilter();
        // 发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 扫描结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 配对状态变化
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 蓝牙开启状态变化
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    public void init() {
        lyricsView = findViewById(R.id.lyricsView);
        title = findViewById(R.id.title);
        seekBar = findViewById(R.id.seekBar);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        reset = findViewById(R.id.reset);
        back = findViewById(R.id.back);

        // 初始化时间
        timeNow = System.currentTimeMillis();
        timeLast = System.currentTimeMillis();
    }

    public void setVolume(int volume) {
        seekBar.setProgress(volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    public void left() {
        content = --content < 0 ? 0 : content;
        geci = getResources().getStringArray(R.array.geci)[content];
        jianpu = getResources().getStringArray(R.array.jianpu)[content];
        lyricsView.resetView(Utils.lyricTransform(geci), Utils.lyricTransform(jianpu));

        title.setText(getResources().getStringArray(R.array.songs)[content]);
    }

    public void right() {
        content = ++content > songsSize - 1 ? songsSize - 1 : content;
        geci = getResources().getStringArray(R.array.geci)[content];
        jianpu = getResources().getStringArray(R.array.jianpu)[content];
        lyricsView.resetView(Utils.lyricTransform(geci), Utils.lyricTransform(jianpu));

        title.setText(getResources().getStringArray(R.array.songs)[content]);
    }

    public void foundBT(final BluetoothDevice device) {
        if (device.getAddress().equals("20:18:04:10:06:56")) {
            bluetoothAdapter.cancelDiscovery();
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        device.createBond();
                    }
                }.start();

            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                isfonded = true;
                if (bluetoothService == null) {
                    bluetoothService = new BluetoothService(LyricActivity.this);
                }
                if (bluetoothService != null) {
                    bluetoothService.connect(device);
                }
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                setType("正在配对");
            }
        }
    }

    public void setType(String str) {
        if (bluetoothDialog != null) {
            bluetoothDialog.setShowText(str);
        }
    }

    public void showDialog() {
        if (bluetoothDialog == null) {
            bluetoothDialog = new BluetoothDialog();
            bluetoothDialog.setOnBluetoothClickListener(new BluetoothDialog.OnBluetoothClickListener() {
                @Override
                public void start() {
                    bluetoothAdapter.startDiscovery();
                }
            });
            ft = getSupportFragmentManager().beginTransaction();
            ft.add(bluetoothDialog, "ProgressDialog");
            if (!bluetoothDialog.isAdded() && !bluetoothDialog.isVisible() && !bluetoothDialog.isRemoving()) {
                ft.commitAllowingStateLoss();
            }
        } else {
            if (!bluetoothDialog.isAdded() && !bluetoothDialog.isVisible() && !bluetoothDialog.isRemoving()) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.add(bluetoothDialog, "ProgressDialog");
                ft.commitAllowingStateLoss();
            }
        }
    }

    public void hideDialog() {
        if (bluetoothDialog != null) {
            bluetoothDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                left();
                break;
            case R.id.right:
                right();
                break;
            case R.id.reset:
                lyricsView.resetView(Utils.lyricTransform(geci), Utils.lyricTransform(jianpu));
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentVolume = progress;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                currentVolume--;
                currentVolume = currentVolume <= 0 ? 0 : currentVolume;
                currentVolume = currentVolume >= maxVolume ? maxVolume : currentVolume;
                setVolume(currentVolume);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                currentVolume++;
                currentVolume = currentVolume <= 0 ? 0 : currentVolume;
                currentVolume = currentVolume >= maxVolume ? maxVolume : currentVolume;
                setVolume(currentVolume);
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                Log.d("123", "KEYCODE_VOLUME_MUTE");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // 确定
            }

            if (resultCode == RESULT_CANCELED) {
                // 取消
                Toast.makeText(this, "拒绝开启蓝牙", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("123", "onDestroy");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }
}
