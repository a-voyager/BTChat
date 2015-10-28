package com.voyager.btchat.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import com.voyager.btchat.Utils.Constant;

import java.io.IOException;

/**
 * Created by wuhaojie on 2015/10/28.
 */
public class AcceptThread extends Thread {
    private final BluetoothServerSocket mServerSocket = null;
    //private BluetoothAdapter mAdapter;
    private String mSocketType;

    public AcceptThread(boolean secure, BluetoothAdapter mAdapter) {
        BluetoothServerSocket tmp = null;
        mSocketType = secure ? "secure" : "Insecure";

        try {
            if (secure) {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(Constant.NAME_SECURE, Constant.MY_UUID_SECURE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
