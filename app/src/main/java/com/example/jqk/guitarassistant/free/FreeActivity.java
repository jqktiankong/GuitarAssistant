package com.example.jqk.guitarassistant.free;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jqk.guitarassistant.R;
import com.example.jqk.guitarassistant.bluetooth.BluetoothDialog;
import com.example.jqk.guitarassistant.event.MessageEvent;
import com.example.jqk.guitarassistant.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import static com.example.jqk.guitarassistant.util.Constants.REQUEST_ENABLE_BT;

public class FreeActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener {

    private ImageView img, back;
    private ImageView guitarImg;
    private RelativeLayout guitarParentView;
    private LinearLayout imgParentView;
    private Button imgd, imga, imgf, imgg, imgc, imge;
    private TextView title;
    private Button play;
    private SeekBar seekBar;
    // 调节音量
    private AudioManager mAudioManager;
    // 系统最大音量
    private int maxVolume;
    // 当前音量
    private int currentVolume;

    private String tune = "";

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
                            Toast.makeText(FreeActivity.this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(FreeActivity.this, "配对失败", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("123", "正在配对");
                            setType("正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("123", "配对成功");
                            Toast.makeText(FreeActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                            foundBT(bluetoothDevice);
                            break;
                        default:
                            Toast.makeText(FreeActivity.this, "未知原因", Toast.LENGTH_SHORT).show();
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
                    case com.example.jqk.guitarassistant.bluetooth.BluetoothService.STATE_NONE:

                        break;
                    case com.example.jqk.guitarassistant.bluetooth.BluetoothService.STATE_LISTEN:

                        break;
                    case com.example.jqk.guitarassistant.bluetooth.BluetoothService.STATE_CONNECTING:
                        setType("正在连接设备");
                        break;
                    case BluetoothService.STATE_CONNECTED:
                        bluetoothAdapter.cancelDiscovery();
                        setType("设备连接成功");
                        Toast.makeText(FreeActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        isConnected = true;
                        hideDialog();
                        break;
                }
                break;
            case Constants.MESSAGE_PLAY:
                timeNow = System.currentTimeMillis();
                if (timeNow - timeLast > 500) {
                    timeLast = timeNow;
                    play();
                }
                break;
            case Constants.MESSAGE_TOAST:
                Toast.makeText(FreeActivity.this, event.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);

        init();

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
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);
        guitarImg = findViewById(R.id.guitarImg);
        guitarParentView = findViewById(R.id.guitarParentView);
        imgParentView = findViewById(R.id.imgParentView);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekBar);
        back = findViewById(R.id.back);

        imgd = findViewById(R.id.imgd);
        imga = findViewById(R.id.imga);
        imgf = findViewById(R.id.imgf);
        imgg = findViewById(R.id.imgg);
        imgc = findViewById(R.id.imgc);
        imge = findViewById(R.id.imge);

        title.setText("自由模式");

        imgd.setOnTouchListener(this);
        imga.setOnTouchListener(this);
        imgf.setOnTouchListener(this);
        imgg.setOnTouchListener(this);
        imgc.setOnTouchListener(this);
        imge.setOnTouchListener(this);

        play.setOnClickListener(this);
        back.setOnClickListener(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 设置当前音量显示
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
        seekBar.setOnSeekBarChangeListener(this);
        // 初始化时间
        timeNow = System.currentTimeMillis();
        timeLast = System.currentTimeMillis();
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
                    bluetoothService = new BluetoothService(FreeActivity.this);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int parentHeight = imgParentView.getMeasuredHeight();
            float imgHeight = parentHeight / 5.0f * 4.0f;
            float imgWidth = imgHeight / 410.0f * 320.0f;
            ViewGroup.LayoutParams lp = img.getLayoutParams();
            lp.width = (int) imgWidth;
            lp.height = (int) imgHeight;
            img.setLayoutParams(lp);

            Log.d("123", "imgHeight = " + (int) imgHeight);
            Log.d("123", "imgWidth = " + (int) imgWidth);

            int guitarParentHeight = guitarParentView.getMeasuredHeight();
            int guitarWidth = guitarParentView.getMeasuredWidth();
            ViewGroup.LayoutParams lp1 = guitarImg.getLayoutParams();
            lp1.width = guitarWidth;
            lp1.height = guitarParentHeight;
            guitarImg.setLayoutParams(lp1);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.imgd:
                        tune = "d";
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_dd);
                        break;
                    case R.id.imga:
                        tune = "a";
                        imgd.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_aa);
                        break;
                    case R.id.imgf:
                        tune = "f";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_ff);
                        break;
                    case R.id.imgg:
                        tune = "g";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_gg);
                        break;
                    case R.id.imgc:
                        tune = "c";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imge.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_cc);
                        break;
                    case R.id.imge:
                        tune = "e";
                        imgd.setOnTouchListener(null);
                        imga.setOnTouchListener(null);
                        imgf.setOnTouchListener(null);
                        imgg.setOnTouchListener(null);
                        imgc.setOnTouchListener(null);

                        img.setImageResource(R.drawable.icon_ee);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                tune = "";
                imgd.setOnTouchListener(this);
                imga.setOnTouchListener(this);
                imgf.setOnTouchListener(this);
                imgg.setOnTouchListener(this);
                imgc.setOnTouchListener(this);
                imge.setOnTouchListener(this);

                img.setImageResource(R.drawable.icon_00);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                play();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    public void playSound(String note) {
        String uriStr = "android.resource://" + getPackageName() + "/";
        Uri uri = null;
        switch (note) {
            case "d":
                uri = Uri.parse(uriStr + R.raw.dd);
                break;
            case "a":
                uri = Uri.parse(uriStr + R.raw.aa);
                break;
            case "f":
                uri = Uri.parse(uriStr + R.raw.ff);
                break;
            case "g":
                uri = Uri.parse(uriStr + R.raw.gg);
                break;
            case "c":
                uri = Uri.parse(uriStr + R.raw.cc);
                break;
            case "e":
                uri = Uri.parse(uriStr + R.raw.ee);
                break;
        }

        final MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();//重置为初始状态
        }
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();//缓冲
            mediaPlayer.start();//开始或恢复播放
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播出完毕事件
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {// 错误处理事件
                @Override
                public boolean onError(MediaPlayer player, int arg1, int arg2) {
                    mediaPlayer.release();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer.release();//释放资源
        }
    }

    public void play() {
        if (tune.equals("")) {
            Toast.makeText(this, "请按键", Toast.LENGTH_SHORT).show();
        } else {
            playSound(tune);
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

    public void setVolume(int volume) {
        seekBar.setProgress(volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }
}
