package com.voyager.btchat;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    }
}
