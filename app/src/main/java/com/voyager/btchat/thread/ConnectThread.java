package com.voyager.btchat.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.voyager.btchat.Utils.Constant;

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

    public ConnectThread(BluetoothDevice mDevice, boolean secure, BluetoothAdapter mAdapter) {
        this.mAdapter = mAdapter;
        BluetoothSocket tmp = null;
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
                Log.e(TAG, "BT close failed...socketType:"+mSocketType);
            }
            connectFailed();
            return;
        }

        /*
        synchronized (){}
        */

        //连接完成 开始监听
        connected(mSocket, mDevice, mSocketType);


    }

    private void connected(BluetoothSocket mSocket, BluetoothDevice mDevice, String mSocketType) {

    }

    private void connectFailed() {

    }
}
