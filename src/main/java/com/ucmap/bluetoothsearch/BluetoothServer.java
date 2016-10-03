package com.ucmap.bluetoothsearch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.MAX_VALUE;

/**
 * <b>@项目名：</b> uPatch<br>
 * <b>@包名：</b>com.tofan.upatch.ui<br>
 * <b>@创建者：</b> cxz<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 优高胜公司<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class BluetoothServer {

    /**
     * 线程执行者
     */
    private ThreadPoolExecutor mExecutor = null;
    /**
     * 控制连接的线程
     */
    private Connect mConnect = null;
    /**
     * 负责读写的线程
     */
    private ConnectedRunnable mConnectedRunnable = null;
    /**
     * 负责接受连接的线程
     */
    private ServerRunnable mServerRunnable = null;

    // Unique UUID
    private static final UUID UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private volatile BluetoothState mCurrentState;
    /**
     * 连接失败
     */
    public static final int MESSAGE_CONNECT_FAIL = 0x03;
    /**
     * 读
     */
    public static final int MESSAGE_READ = 0x01;
    //write
    public static final int MESSAGE_WRITE = 0x02;
    /**
     * disconnection
     */
    public static final int MESSAGE_DISCONNECTION = 0x10;
    public static final int MESSAGE_CONNECTED = 0x05;
    /**
     * 连接中断
     */
    public static final int CONNECT_CUT = 0x08;
    //程序是否关闭
    private volatile boolean isClose = false;
    //通信的Handler
    private Handler mHandler;

    public BluetoothState getCurrentState() {
        return mCurrentState;
    }

    public void setCurrentState(BluetoothState currentState) {
        mCurrentState = currentState;
    }

    private BluetoothAdapter mBluetoothAdapter;


    private static String deviceName;
    private static String deviceAddress;

    public BluetoothServer(Handler handler) {

        this.mHandler = handler;
//        Log.i("Infoss", "handler:" + mHandler);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceName = mBluetoothAdapter.getName();
        deviceAddress = mBluetoothAdapter.getAddress();
    }

    static enum BluetoothState {
        DISCONNECTION, CONNECTING, CONNECTED, LISTEN;
    }

    //启动蓝牙服务
    public void start() {

        if (isClose)
            return;

        closeAll();

//        Log.i("Infoss", "perform start");
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = new ThreadPoolExecutor(1, MAX_VALUE, 1000 * 30, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
        }
        setCurrentState(BluetoothState.LISTEN);
        mServerRunnable = new ServerRunnable(this, mBluetoothAdapter);
        mExecutor.execute(mServerRunnable);


    }

    public void connect(BluetoothDevice bluetoothDevice) {
        closeAll();
//        Log.i("Infoss", "启动 连接..." + bluetoothDevice.getAddress() + "  h:" + mHandler);
        BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());
        mConnect = new Connect(mBluetoothDevice.getAddress(), mBluetoothAdapter, mHandler, this);
        mExecutor.execute(mConnect);
        setCurrentState(BluetoothState.CONNECTING);
    }

    //等待连接的Runnable
    static class ServerRunnable implements Runnable {

        private BluetoothServer mBluetoothServer;
        private BluetoothAdapter mBluetoothAdapter;
        private String name = "Secure";
        private BluetoothServerSocket mBluetoothServerSocket;


        public ServerRunnable(BluetoothServer bluetoothServer, BluetoothAdapter bluetoothAdapter) {
            this.mBluetoothServer = bluetoothServer;
            this.mBluetoothAdapter = bluetoothAdapter;
        }

        @Override
        public void run() {

            //只要当前状态不等连接状态
            while (mBluetoothServer != null && (mBluetoothServer.getCurrentState() == BluetoothState.LISTEN == true)) {
                try {
//                    Log.i("Infoss", "开始监听 state:" + mBluetoothServer.getCurrentState() + "  thread:" + Thread.currentThread().getName());
                    mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, BluetoothServer.UUID_SECURE);
//                    Log.i("Infoss", "等待连接...");
                    BluetoothSocket mBluetoothSocket;
                    mBluetoothSocket = mBluetoothServerSocket.accept(); //Thread come to block
                    if (mBluetoothSocket != null) {

                        mBluetoothServer.connected(mBluetoothSocket);

                    }
                } catch (Exception e) {
//                    Log.i("Infoss", "接受失败.." + e);
                    e.printStackTrace();

                } finally {
                    try {
//                        Log.i("Infoss", "关闭了... state:" + mBluetoothServer.getCurrentState());
                        if (mBluetoothServerSocket != null) {
                            mBluetoothServerSocket.close();

                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
//                        Log.i("Infoss", "失败...");
                        break;
                    }
                }

            }
        }

        private void connect_cut() {

        }

        private void close() {

            try {
                if (mBluetoothServerSocket != null) {
//                    Log.i("Infoss", "关闭了2...");
                    mBluetoothServer.setCurrentState(BluetoothState.DISCONNECTION);
                    mBluetoothServerSocket.close();
                    mBluetoothServerSocket = null;
                }
            } catch (Exception e) {
//                Log.i("Infoss", "关闭 fail...");
                e.printStackTrace();
            }
        }
    }

    //have bean connected
    private void connected(BluetoothSocket bluetoothSocket) {

        closeAll();
        mConnectedRunnable = new ConnectedRunnable(bluetoothSocket, mHandler, this);
        this.setCurrentState(BluetoothState.CONNECTED);
        mExecutor.execute(mConnectedRunnable);
        if (mHandler != null)
            mHandler.obtainMessage(MESSAGE_CONNECTED, bluetoothSocket.getRemoteDevice().getName()).sendToTarget();

    }

    /**
     * 关闭所有执行的线程
     */
    private void closeAll() {

        if (mConnect != null) {
            mConnect.close();
            mConnect = null;
        }
        if (mConnectedRunnable != null) {
            mConnectedRunnable.close();
            mConnectedRunnable = null;
        }
        setCurrentState(BluetoothState.DISCONNECTION);
        if (mServerRunnable != null) {
            mServerRunnable.close();
            mServerRunnable = null;
        }
    }


    //已连接的Runnable
    static class ConnectedRunnable implements Runnable {

        private Handler mHandler;
        private BluetoothSocket mBluetoothSocket;
        private BluetoothServer mBluetoothServer;
        private InputStream mIs;
        private OutputStream mOs;

        public ConnectedRunnable(BluetoothSocket bluetoothSocket, Handler handler, BluetoothServer bluetoothServer) {
            mHandler = handler;
            mBluetoothSocket = bluetoothSocket;
            mBluetoothServer = bluetoothServer;
            try {
                mIs = mBluetoothSocket.getInputStream();
                mOs = mBluetoothSocket.getOutputStream();
            } catch (Exception e) {
                connect_cut();
                e.printStackTrace();
//                Log.i("Infoss", "获取流失败..." + mIs + "   os:" + mOs);
            }
        }

        @Override
        public void run() {

            while (mBluetoothServer.getCurrentState() == BluetoothState.CONNECTED && mIs != null) {
                try {
//                    if (mIs.available() != 0)
//                        return;
                    byte[] b = new byte[1024];
                    int length = mIs.read(b);
                    if (length != -1) {
                        Message mMessage = mHandler.obtainMessage();
                        mMessage.what = MESSAGE_READ;
                        mMessage.obj = new com.ucmap.bluetoothsearch.Message(new String(b, 0, length),
                                MESSAGE_READ,
                                mBluetoothSocket.getRemoteDevice().getName(),
                                mBluetoothSocket.getRemoteDevice().getAddress()
                                , Utils.getCurrentTime(null));
                        mMessage.sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    connect_cut();
                }

            }

        }

        private void connect_cut() {

            if (mHandler != null)
                mHandler.obtainMessage(BluetoothServer.CONNECT_CUT, "Disconnection").sendToTarget();
            if (mBluetoothServer != null) {
                mBluetoothServer.start();
            }
        }

        //write message to other
        public void write(String message) {

            try {

                if (mOs != null && mHandler != null) {
                    Log.i("Infoss", "have been write");
                    mOs.write(message.getBytes("utf-8"));
                    Message mMessage = mHandler.obtainMessage();
                    mMessage.what = MESSAGE_WRITE;
                    mMessage.obj = new com.ucmap.bluetoothsearch.Message(message, MESSAGE_WRITE,
                            BluetoothServer.deviceName==null?"Unkonw":BluetoothServer.deviceName,
                            BluetoothServer.deviceAddress==null?"Unkonw":BluetoothServer.deviceAddress
                            , Utils.getCurrentTime(null));
                    mMessage.sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void close() {

            try {
                if (mOs != null)
                    mOs.close();
                if (mIs != null)
                    mIs.close();
                if (mBluetoothSocket != null)
                    mBluetoothSocket.close();

                mOs = null;
                mIs = null;
                mBluetoothSocket = null;
                mBluetoothServer.setCurrentState(BluetoothState.DISCONNECTION);

            } catch (Exception e) {
                e.printStackTrace();
//                Log.i("Infoss", "close exception");
            }


        }

    }


    private static class Connect implements Runnable {

        private BluetoothAdapter mBluetoothAdapter;
        private BluetoothDevice mBluetoothDevice;
        private Handler mHandler;
        private BluetoothServer mBluetoothServer;

        public Connect(String address, BluetoothAdapter bluetoothAdapter, Handler handler, BluetoothServer bluetoothServer) {
            mBluetoothAdapter = bluetoothAdapter;
            mBluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            this.mHandler = handler;
            mBluetoothServer = bluetoothServer;
        }

        @Override
        public void run() {

            BluetoothSocket mBluetoothSocket = null;
            try {
//                Log.i("Infoss", "连接 Connect");
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID_SECURE);

                if (mBluetoothSocket != null) {
                    mBluetoothSocket.connect();
//                    Log.i("Infoss", "sucess Connect");
                    mBluetoothServer.connected(mBluetoothSocket);
                    mBluetoothServer.setCurrentState(BluetoothState.CONNECTED);
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.i("Infoss", "create fail");
                connectFail("Connect fail");
            }

        }

        public void close() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter = null;
            if (mHandler != null)
                mHandler = null;

        }

        private void connectFail(String msg) {
//            Log.i("Infoss", "Handler:" + mHandler + "    msg:" + msg);
            if (mHandler != null)
                mHandler.obtainMessage(MESSAGE_CONNECT_FAIL, msg).sendToTarget();
        }

    }

    public void sendMessage(String message) {

        if (mConnectedRunnable == null || getCurrentState() != BluetoothState.CONNECTED)
            return;
        mConnectedRunnable.write(message);
    }

    public void close() {
        isClose = true;
        closeAll();
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
        mHandler.obtainMessage(MESSAGE_DISCONNECTION, "Disconnection").sendToTarget();
        mHandler = null;
    }


}
