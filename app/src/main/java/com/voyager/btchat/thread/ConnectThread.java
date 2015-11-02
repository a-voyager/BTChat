package com.voyager.btchat.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.voyager.btchat.Utils.Constant;
import com.voyager.btchat.service.BluetoothChatService;

import java.io.IOException;

/**
 * 主动连接其它蓝牙设备线程类
 * Created by wuhaojie on 2015/10/29.
 */
public class ConnectThread extends Thread {
    private static final String TAG = "ConnectedThread";
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private String mSocketType;
    private BluetoothAdapter mAdapter;
    private BluetoothChatService mBluetoothChatService;
    private ConnectThread mConnetThread;
    private ConnectedThread mConnetedThread;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInSecureAcceptThread;

    public ConnectThread(BluetoothDevice mDevice, boolean secure, BluetoothAdapter mAdapter, BluetoothChatService mBluetoothChatService, ConnectThread mConnetThread, ConnectedThread mConnetedThread, AcceptThread mSecureAcceptThread, AcceptThread mInSecureAcceptThread) {
        this.mAdapter = mAdapter;
        this.mBluetoothChatService = mBluetoothChatService;
        this.mConnetedThread = mConnetedThread;
        this.mSecureAcceptThread = mSecureAcceptThread;
        this.mInSecureAcceptThread = mInSecureAcceptThread;
        BluetoothSocket tmp = null;
        this.mConnetThread = mConnetThread;
        this.mDevice = mDevice;
        mSocketType = secure ? "secure" : "Insecure";

        try {
            if (secure) {
                tmp = mDevice.createRfcommSocketToServiceRecord(Constant.MY_UUID_SECURE);
            } else {
                tmp = mDevice.createInsecureRfcommSocketToServiceRecord(Constant.MY_UUID_INSECURE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }


    @Override
    public void run() {
        Log.i(TAG, "begin ConnectThread...  SocketType:" + mSocketType);
        setName("ConnectThread" + mSocketType);   //设置线程名称

        //连接蓝牙取消搜索
        mAdapter.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "BT close failed...socketType:" + mSocketType);
            }
            mBluetoothChatService.connectedFailed();
            return;
        }


        synchronized (mBluetoothChatService) {
            mConnetThread = null;
        }

        //连接完成 开始监听
        mBluetoothChatService.connected(mSocket, mDevice, mSocketType);


    }
/*
    private void connected(BluetoothSocket mSocket, BluetoothDevice mDevice, String socketType) {
        Log.d(TAG, "connected, SocketType="+socketType);

        //已完成连接 结束ConnectThread
        if (mConnetThread != null){
            mConnetThread.cancel();
            mConnetThread = null;
        }
        //结束所有正在运行的ConnectedThread
        if (mConnetedThread != null){
            mConnetedThread.cancel();
            mConnetedThread = null;
        }
        if (mSecureAcceptThread != null){
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInSecureAcceptThread != null){
            mInSecureAcceptThread.cancel();
            mInSecureAcceptThread = null;
        }




    }

    private void connectFailed() {

    }
    */

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() failed, socketType = " + mSocket, e);
        }
    }
}
