package com.voyager.btchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private Button btn_scan;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> pairedArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);

        initView();
        initList();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, intentFilter);

        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, intentFilter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if (bondedDevices.size() > 0) {
            findViewById(R.id.dl_title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : bondedDevices) {
                pairedArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedArrayAdapter.add("没有已配对设备");
        }


    }

    /**
     * 初始化列表
     */
    private void initList() {
        //Array Adapter 已配对设备列表 新发现设备列表
        pairedArrayAdapter = new ArrayAdapter<>(this, R.layout.dl_device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.dl_device_name);

        ListView lv_paired_devices = (ListView) findViewById(R.id.dl_paired_devices);
        lv_paired_devices.setAdapter(pairedArrayAdapter);
        lv_paired_devices.setOnItemClickListener(mOnItemClickListener);
    }

    private void initView() {
        btn_scan = (Button) findViewById(R.id.dl_btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
    }

    private void doDiscovery() {

    }


    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


}
