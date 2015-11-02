package com.voyager.btchat.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.voyager.btchat.Utils.Constant;
import com.voyager.btchat.service.BluetoothChatService;

import java.io.IOException;

/**
 * 监听外来蓝牙请求线程类
 * Created by wuhaojie on 2015/10/28.
 */
public class AcceptThread extends Thread {
    private static final String TAG = "AcceptThread";
    private final BluetoothChatService mBluetoothChatService;
    //服务端socket
    private BluetoothServerSocket mServerSocket = null;
    //private BluetoothAdapter mAdapter;
    //连接类型
    private String mSocketType;
    //连接状态：Constant接口
    private int mState;

    public AcceptThread(boolean secure, BluetoothAdapter mAdapter, BluetoothChatService bluetoothChatService) {
        this.mBluetoothChatService = bluetoothChatService;
        BluetoothServerSocket tmp = null;
        mSocketType = secure ? "secure" : "Insecure";

        try {
            if (secure) {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(Constant.NAME_SECURE, Constant.MY_UUID_SECURE);
            } else {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(Constant.NAME_SECURE, Constant.MY_UUID_SECURE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = tmp;
    }

    @Override
    public void run() {
        Log.d(TAG, "AcceptThread Run: SocketType:" + mSocketType);
        setName("AcceptThread" + mSocketType);      //???
        BluetoothSocket socket = null;

        //只要没有连接就一直监听
        while (mState != Constant.STATE_CONNECTED) {
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "SocketType: " + mSocketType + "accept() failed...");
                break;
            }

            if (socket != null) {
                synchronized (mBluetoothChatService) {
                }
            }


        }
    }


    public void cancel() {
        Log.d(TAG, "SocketType: " + mSocketType + "cancel() " + this);
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "SocketType: " + mSocketType + "cancel() failed" + this);
        }

    }


}
