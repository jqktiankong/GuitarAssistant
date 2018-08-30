package com.example.jqk.guitarassistant.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.jqk.guitarassistant.event.MessageEvent;
import com.example.jqk.guitarassistant.util.Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BluetoothService {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    //蓝牙适配器
    private BluetoothAdapter mAdapter;

    private ConnectThread mConnectThread;// 连接一个设备的进程
    private ConnectedThread mConnectedThread;

    private int mState;// 当前状态

    // 指明连接状态的常量
    public static final int STATE_NONE = 0;         //没有连接
    public static final int STATE_LISTEN = 1;       //等待连接
    public static final int STATE_CONNECTING = 2;  //正在连接
    public static final int STATE_CONNECTED = 3;   //已经连接

    public BluetoothService(Context context) {
        this.context = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器
        mState = STATE_NONE; //当前连接状态：未连接
    }

    public synchronized void connect(BluetoothDevice device) {

        //连接一个蓝牙时，将该设备 的蓝牙连接线程关闭，如果有的话
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);// Get a BluetoothSocket for a connection with the given BluetoothDevice
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {

            setName("ConnectThread");

            try {
                mmSocket.connect();// This is a blocking call and will only return on a successful connection or an exception
            } catch (IOException e) {
//                connectionFailed();
                return;
            }

            synchronized (BluetoothService.this) {// Reset the ConnectThread because we're done
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);// Start the connected thread
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private Queue<Byte> queueBuffer = new LinkedList<Byte>();
        private byte[] packBuffer = new byte[11];


        //构造方法
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        // 数组大小看你的数据需求，这里存的是你处理蓝牙传输来的字节数据之后实际要用到的数据
        private float[] fData = new float[31];

        @Override
        public void run() {
            byte[] tempInputBuffer = new byte[1024];
            int acceptedLen = 0; //记录每次读取数据的数据长度
            byte sHead;
            long lLastTime = System.currentTimeMillis(); //获取开始时间
            while (true) {
                try {
                    acceptedLen = mmInStream.read(tempInputBuffer);//返回接收的长度
                    //从缓冲区中读取数据
                    for (int i = 0; i < acceptedLen; i++) {
                        queueBuffer.add(tempInputBuffer[i]);
                    }
                    // 这里需要按个人硬件数据的情况自行修改了
                    // 如果你的硬件蓝牙传输 一个包有11个字节，那queueBuffer.size()>=11
                    // 如果你的硬件蓝牙传输 一个包有21个字节，那queueBuffer.size()>=21
                    while (queueBuffer.size() >= 11) {
                        //返回队首并删除，判断队首是不是0x55，如果不是，说明不是一个包的数据，跳过，
                        //注意这里的0x55是你的包的首字节
                        if (queueBuffer.poll() != 0x55)
                            continue;
                        // 进入到这里，说明得到一个包的数据了，然后就要根据个人硬件的数据情况，将byte类型的数据转换为float类型的数据

                        sHead = queueBuffer.poll(); //返回队首并删除

// 现在得到的就是你数据部分了，如果有9位字节代表数据，j<9 ，如果有19位字节代表数据，j<19

//将字节数组存到packBuffer[]数据中，用于byte-->float数据的转换
                        for (int j = 0; j < 9; j++) {
                            packBuffer[j] = queueBuffer.poll();
                        }
                        switch (sHead) {
                            case 0x51://角速度
                                fData[3] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 16;
//                                fData[4] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 16;
                                break;
                        }
//                        Log.d("123", "X轴加速度低位 = " + fData[3]);
//                        Log.d("123", "X轴加速度高位 = " + fData[4]);
                    }
                    long lTimeNow = System.currentTimeMillis(); // 获取收据转换之后的时间
                    // 如果数据处理后的时间  与 接收到数据的时间 的时间差>80 则发送消息传输数据，
                    // 这个时间需要看你硬件一秒钟发送的包的个数
//                    if (lTimeNow - lLastTime > 0) {
//                        lLastTime = lTimeNow;
                    if (fData[3] > 1) {
                        EventBus.getDefault().post(new MessageEvent(Constants.MESSAGE_PLAY, "", -1));
                    }
//                    }
                } catch (IOException e) {
                    connectionLost();
                    return;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                mmSocket = null;
            } catch (IOException e) {

            }
        }
    }

    //连接失败
    private void connectionFailed() {
        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        EventBus.getDefault().post(new MessageEvent(Constants.MESSAGE_TOAST, "连接失败", -1));
    }

    // 连接丢失
    private void connectionLost() {
        EventBus.getDefault().post(new MessageEvent(Constants.MESSAGE_TOAST, "设备丢失", -1));
    }


    //用于 蓝牙连接的Activity onResume()方法
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        setState(STATE_LISTEN);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);

        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        EventBus.getDefault().post(new MessageEvent(Constants.MESSAGE_STATE_CHANGE, "", state));
    }

    public synchronized void stop() {

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }
}
