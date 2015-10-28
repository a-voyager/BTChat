package com.voyager.btchat.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.voyager.btchat.Utils.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙连接成功后的读写线程类
 * Created by wuhaojie on 2015/10/29.
 */
public class ConnectedThread extends Thread {
    private static final String TAG = "ConnectedThread";
    private final BluetoothSocket mSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private Handler mHandler = new Handler();


    public ConnectedThread(BluetoothSocket socket, String socketType) {
        this.mSocket = socket;
        InputStream tmpIS = null;
        OutputStream tmpOS = null;

        try {
            tmpIS = socket.getInputStream();
            tmpOS = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "IO stream created failed...", e);
        }
        mInputStream = tmpIS;
        mOutputStream = tmpOS;
    }


    @Override
    public void run() {
        Log.i(TAG, "begin ConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = mInputStream.read(buffer);

                mHandler.obtainMessage(Constant.MESSAGE_READ, bytes, -1).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread run() failed");
                connectionLost();
            }

        }

    }


    public void write(byte[] buffer) {
        try {
            mOutputStream.write(buffer);
            mHandler.obtainMessage(Constant.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

        } catch (IOException e) {
            Log.e(TAG, "write() failed");
        }
    }

    private void connectionLost() {

    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() failed...");
        }
    }

}
