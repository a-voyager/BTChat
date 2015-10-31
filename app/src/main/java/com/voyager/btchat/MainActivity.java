package com.voyager.btchat;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.voyager.btchat.Utils.Constant;
import com.voyager.btchat.service.BluetoothChatService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView main_lv;
    private EditText et_sendmessage;
    private Button btn_send;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothChatService = null;
    private BluetoothChatService mChatService;
    private StringBuffer mOutStringBuffer;
    private String mConnecedDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("蓝牙聊天");
        setContentView(R.layout.activity_main);
        initView();
        initData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.e(TAG, "设备不支持蓝牙");
            Toast.makeText(MainActivity.this, "对不起，您的设备没有蓝牙模块", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initData() {

    }

    private void initView() {
        btn_send = (Button) findViewById(R.id.btn_send);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        main_lv = (ListView) findViewById(R.id.main_list_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //打开蓝牙设备
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, Constant.REQUEST_ENABLE_BT);
        } else if (mBluetoothChatService == null) {
            setupChat();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothChatService != null) {
            mBluetoothChatService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothChatService != null) {
            if (mBluetoothChatService.getState() == BluetoothChatService.STATE_NONE) {
                mBluetoothChatService.start();
            }
        }


    }

    private void setupChat() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mChatService = new BluetoothChatService(this, mHandler);

        mOutStringBuffer = new StringBuffer("");


    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(MainActivity.this, "未连接蓝牙设备!", Toast.LENGTH_SHORT).show();
            et_sendmessage.setText("");
            return;
        }
        String text = et_sendmessage.getText().toString();
        if (text.length() > 0) {
            byte[] textBytes = text.getBytes();
            mChatService.write(textBytes);
            mOutStringBuffer.setLength(0);
            et_sendmessage.setText(mOutStringBuffer);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevices(data, true);
                }
                break;
            case Constant.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(MainActivity.this, "蓝牙开启失败！", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * 连接蓝牙设备
     */
    private void connectDevices(Intent data, boolean secure) {
        String addr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
        mBluetoothChatService.connect(device, secure);
    }


    /**
     * 确保本设备对外可见
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(1, mConnecedDeviceName, "连接到 " + mConnecedDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus("连接中...");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus("无连接");
                            break;
                    }
                    break;
                case Constant.MESSAGE_WRITE:
                    break;
                case Constant.MESSAGE_READ:
                    break;
                case Constant.MESSAGE_DEVICE_NAME:
                    mConnecedDeviceName = msg.getData().getString(Constant.DEVICE_NAME);
                    break;
                case Constant.MESSAGE_TOAST:
                    break;
            }
        }
    };

    private void setStatus(int i, String mConnecedDeviceName, String s) {

    }

    private void setStatus(CharSequence title) {

    }


}
