package com.voyager.btchat.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.voyager.btchat.Utils.Constant;
import com.voyager.btchat.thread.AcceptThread;
import com.voyager.btchat.thread.ConnectThread;
import com.voyager.btchat.thread.ConnectedThread;

/**
 * 提供蓝牙控制服务
 * Created by wuhaojie on 2015/10/29.
 */
public class BluetoothChatService {
    private static final String TAG = "BluetoothChatService";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private final Handler mHandler;
    private final BluetoothAdapter mAdapter;

    private static AcceptThread mSecureAcceptThread;
    private static AcceptThread mInSecureAcceptThread;
    private static ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;

    private int mState;


    /**
     * constructor
     */
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * getter
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * setter
     */
    public synchronized void setState(int mState) {
        this.mState = mState;
        mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
    }


    /**
     * 开启BluetoothChatService服务
     * 监听模式AcceptThread
     * calling by onResume in MainActivity
     */
    public synchronized void start() {
        Log.d(TAG, "bluetoothChatService start()");

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        setState(STATE_LISTEN);

        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true, mAdapter, this);
            mSecureAcceptThread.start();
        }
        if (mInSecureAcceptThread == null) {
            mInSecureAcceptThread = new AcceptThread(false, mAdapter, this);
            mInSecureAcceptThread.start();
        }

    }

    /**
     * kill all
     */
    public synchronized void stop() {
        Log.d(TAG, "stop()");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInSecureAcceptThread != null) {
            mInSecureAcceptThread.cancel();
            mInSecureAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);
        //取消所有线程并尝试启用安全连接
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        //取消当前已连接线程
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device, secure, mAdapter, this, mConnectThread, mConnectedThread, mSecureAcceptThread, mInSecureAcceptThread);
        mConnectThread.start();
        setState(STATE_CONNECTING);

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Log.d(TAG, "connected, socketType = " + socketType);

        // after connect, kill ConnectThread
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // kill all ConnectedThread
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // kill all AcceptThread
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInSecureAcceptThread != null) {
            mInSecureAcceptThread.cancel();
            mInSecureAcceptThread = null;
        }

        //开启连接线程
        mConnectedThread = new ConnectedThread(socket, socketType, this, mHandler);
        mConnectedThread.start();

        //将已经连接的蓝牙设备名称发送给UI Activity
        Message message = mHandler.obtainMessage(Constant.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.DEVICE_NAME, device.getName());
        message.setData(bundle);
        mHandler.sendMessage(message);
        setState(STATE_CONNECTED);

    }


    public void write(byte[] out) {
        ConnectedThread cThread;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            cThread = mConnectedThread;
        }
        cThread.write(out);
    }

    /**
     * 连接失败
     * 尝试重新启动监听模式并且通知 UI层
     */
    public void connectedFailed() {
        Message message = mHandler.obtainMessage(Constant.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TOAST, "Unable to connect device");
        message.setData(bundle);
        mHandler.sendMessage(message);
        //restart for listening mode
        BluetoothChatService.this.start();
    }

    /**
     * 连接丢失 通知UI层
     */
    public void connectionLost() {
        Message message = mHandler.obtainMessage(Constant.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TOAST, "Device connection was lost");
        message.setData(bundle);
        mHandler.sendMessage(message);
    }


}
