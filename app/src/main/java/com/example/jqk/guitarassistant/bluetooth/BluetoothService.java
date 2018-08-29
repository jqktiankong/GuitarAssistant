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

import com.example.jqk.guitarassistant.util.Constants;

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
    private Handler mHandler;

    //当前传感器设备的个数，即要开启的线程个数，用于设置线程数组的大小
    //这里默认为1，因为我们目前只需要和一个传感器连接， 比如：你要连接两个硬件设备，那就设置值为2，这样就会开启两个线程，分别去执行想要操作
    public static final int SENSEOR_NUM = 1;

    //    private AcceptThread mAcceptThread;// 请求连接的监听进程
    private ConnectThread mConnectThread;// 连接一个设备的进程
    public ConnectedThread[] mConnectedThread = new ConnectedThread[SENSEOR_NUM];// 已经连接之后的管理进程

    private int mState;// 当前状态

    // 指明连接状态的常量
    public static final int STATE_NONE = 0;         //没有连接
    public static final int STATE_LISTEN = 1;       //等待连接
    public static final int STATE_CONNECTING = 2;  //正在连接
    public static final int STATE_CONNECTED = 3;   //已经连接

    public BluetoothService(Context context, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
        mAdapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器
        mState = STATE_NONE; //当前连接状态：未连接
    }

    // 参数 index 是 硬件设备的id ，随便设的，目的在于当 同时连接多个硬件设备的时候，根据此id进行区分
    public synchronized void connect(BluetoothDevice device, int index) {

        //连接一个蓝牙时，将该设备 的蓝牙连接线程关闭，如果有的话
        //demo  就只有一个硬件设备，默认该设备id 取值index=1;
        if (mConnectedThread[index - 1] != null) {
            mConnectedThread[index - 1].cancel();
            mConnectedThread[index - 1] = null;
        }
        mConnectThread = new ConnectThread(device, index);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private int index;

        public ConnectThread(BluetoothDevice device, int index) {
            mmDevice = device;
            this.index = index;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);// Get a BluetoothSocket for a connection with the given BluetoothDevice
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {

            setName("ConnectThread");
            //当连接成功，取消蓝牙适配器搜索蓝牙设备的操作，因为搜索操作非常耗时
            mAdapter.cancelDiscovery();// Always cancel discovery because it will slow down a connection

            try {
                mmSocket.connect();// This is a blocking call and will only return on a successful connection or an exception
            } catch (IOException e) {
                connectionFailed(this.index);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                }

                BluetoothService.this.start();// 引用来说明要调用的是外部类的方法 run
                return;
            }

            synchronized (BluetoothService.this) {// Reset the ConnectThread because we're done
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice, index);// Start the connected thread
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
        private int index;
        private Queue<Byte> queueBuffer = new LinkedList<Byte>();
        private byte[] packBuffer = new byte[11];


        //构造方法
        public ConnectedThread(BluetoothSocket socket, int index) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.index = index;
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
                        Log.d("123", "拨弦");
                        Message msg = mHandler.obtainMessage(Constants.MESSAGE_PLAY);
                        mHandler.sendMessage(msg);
                    }
//                    }
                } catch (IOException e) {

                    e.printStackTrace();
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
    private void connectionFailed(int index) {
        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "未能连接设备" + index);
        bundle.putInt("device_id", index);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    // 连接丢失
    private void connectionLost(int index) {
        if (mHandler != null) {
            setState(STATE_LISTEN);
            Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast", "设备丢失" + index);
            bundle.putInt("device_id", index);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }


    //用于 蓝牙连接的Activity onResume()方法
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

//        if (mAcceptThread == null) {
//            mAcceptThread = new AcceptThread();
//            mAcceptThread.start();
//        }
        setState(STATE_LISTEN);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, int index) {
        Log.d("MAGIKARE", "连接到线程" + index);
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
//        if (mAcceptThread != null) {
//            mAcceptThread.cancel();
//            mAcceptThread = null;
//        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread[index - 1] = new ConnectedThread(socket, index);

        mConnectedThread[index - 1].start();

        setState(STATE_CONNECTED);
    }

    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        }
    }

//    private class AcceptThread extends Thread {
//        // The local server socket
//        private final BluetoothServerSocket mmServerSocket;
//
//        //private int index;
//        public AcceptThread() {
//            BluetoothServerSocket tmp = null;
//
//            // this.index=index;
//            // Create a new listening server socket
//            try {
//                tmp = mAdapter.listenUsingRfcommWithServiceRecord("BluetoothData", MY_UUID);
//            } catch (IOException e) {
//            }
//            mmServerSocket = tmp;
//        }
//
//        public volatile boolean exit = false;
//
//        public void run() {
//            while (!exit) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
//            }
//        }
//
//        public void cancel() {
//
//            try {
//                if (mmServerSocket != null) {
//                    mmServerSocket.close();
//                    exit = true;
//                    this.join();
//                }
//            } catch (IOException e) {
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public synchronized int getState() {
        return mState;
    }


    public synchronized void stop() {

        mHandler.removeCallbacksAndMessages(null);

        if (mConnectedThread != null) {
            for (int i = 0; i < mConnectedThread.length; i++) {
                if (mConnectedThread[i] != null) {
                    mConnectedThread[i].cancel();
                }

            }
            mConnectedThread = null;
        }

//        if (mAcceptThread != null) {
//            mAcceptThread.cancel();
//            mAcceptThread = null;
//        }
        setState(STATE_NONE);
    }
}
