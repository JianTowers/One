package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothAct extends AppCompatActivity {
    Button btOpenB,btCloseB,btAllowB,btEquipB;

    BluetoothAdapter bluetoothAdapter;
    //用来存放搜到的蓝牙
    private Set<BluetoothDevice> mDevices;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // 注册扫描监听器，一般写在onCreate()函数中执行
        IntentFilter filter=new IntentFilter();
//发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
//连接断开
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//完成扫描
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(searchReceiver,filter);


        btOpenB = (Button) findViewById(R.id.btOpenB);
        btAllowB = (Button) findViewById(R.id.btAllowB);
        btCloseB = (Button) findViewById(R.id.btCloseB);
        btEquipB = (Button) findViewById(R.id.btEquipB);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btOpenB.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,0);
            }
        });

        btAllowB.setOnClickListener(v -> {
            if (bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent,0);
            }
        });

        btEquipB.setOnClickListener(v -> {
            if (bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
//            SystemClock.sleep(10000);
//            mDevices = bluetoothAdapter.getBondedDevices();
//            for (BluetoothDevice bluetoothDevice:mDevices){
//                LogUtils.d(bluetoothDevice.getName());
//            }
        });

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        spinner.setAdapter(new MaterialSpinnerAdapter<>(this,list));
    }

    // 监听器监听蓝牙扫描结果
    BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //获取蓝牙搜索到的BluetoothDevice对象
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //getName()方法，获取蓝牙名称
                    if (device.getName() == null) {
                        return;
                    }else {
                        LogUtils.d(device.getName());
                    }
                }
            }
        }
    };

}