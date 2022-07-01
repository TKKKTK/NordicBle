package com.wg.nordicbletest;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.ConnectRequest;

public class DeviceActivity extends AppCompatActivity {
    private DeviceManager deviceManager;
    private BleDevice device;
    private ConnectRequest connectRequest;

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);

        final Intent intent = getIntent();
        device = intent.getParcelableExtra(EXTRA_DEVICE);
        Log.d(TAG, "onCreate: " + device.getName() + " " + device.getAddress());
         deviceManager = new DeviceManager(getApplication());
        connect();
    }

    public void connect(){
        if (device != null){
            connectRequest = deviceManager.connect(device.getDevice())
                    .retry(3,100)
                    .useAutoConnect(false)
                    .then(d -> connectRequest = null);
            connectRequest.enqueue();
        }
    }
}