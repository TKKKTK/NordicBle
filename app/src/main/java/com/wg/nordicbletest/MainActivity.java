package com.wg.nordicbletest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MainActivity extends AppCompatActivity implements DeviceAdapter.OnItemClickListener {

    private List<BleDevice> bleDevices = new ArrayList<>();
    private DeviceAdapter deviceAdapter;

    //请求打开蓝牙
    private static final int REQUEST_ENABLE_BLUETOOTH = 100;
    //权限请求码
    private static final int REQUEST_PERMISSION_CODE = 9527;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        openBluetooth();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        deviceAdapter = new DeviceAdapter(bleDevices);
        deviceAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(deviceAdapter);

    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,Menu.FIRST,0,"开始扫描");
        menu.add(Menu.NONE,2,0,"停止扫描");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单选择
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case Menu.FIRST:
                startScan();
                break;
            case 2:
                stopScan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startScan(){
        // Scanning settings
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(500)
                .setUseHardwareBatchingIfSupported(false)
                .build();

        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.startScan(null, settings, scanCallback);
    }

    public void openBluetooth(){
        //获取蓝牙适配器
      BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null){//是否支持蓝牙
            if(bluetoothAdapter.isEnabled()){
                //蓝牙已打开
//                showMsg("蓝牙已打开");
            }else{
                //startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),REQUEST_ENABLE_BLUETOOTH);
            }
        }else{
            //设备不支持蓝牙
           // showMsg("设备不支持蓝牙");
        }

    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results){
                BleDevice bleDevice = new BleDevice(result);
                if (indexOf(bleDevice) == -1){
                    bleDevices.add(bleDevice);
                    deviceAdapter.notifyItemInserted(bleDevices.size()-1);
            }

                String name = bleDevice.getName();
                String address = bleDevice.getAddress();
                Log.d(TAG, "onBatchScanResults: "+name+ " " + address );
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w("ScannerViewModel", "Scanning failed with code " + errorCode);

            if (errorCode == ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
                stopScan();
                startScan();
            }
        }
    };

    //去重判断
    public int indexOf(BleDevice bleDevice){
        int i = 0;
        for (BleDevice device : bleDevices){
            if (device.getAddress().equals(bleDevice.getAddress()))
                return i;
            i++;
        }
        return -1;
    }

    public void stopScan(){
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
    }

    //动态权限申请
    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String[] perms ={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,perms,REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onItemClick(BleDevice bleDevice) {
        //点击连接
        Log.d(TAG, "onItemClick: 点击连接");
        final Intent controlDeviceIntent = new Intent(this,DeviceActivity.class);
        controlDeviceIntent.putExtra(DeviceActivity.EXTRA_DEVICE,bleDevice);
        startActivity(controlDeviceIntent);
    }
}