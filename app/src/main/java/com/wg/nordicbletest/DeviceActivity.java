package com.wg.nordicbletest;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.ConnectRequest;

public class DeviceActivity extends AppCompatActivity implements UIChangeListener{
    private DeviceManager deviceManager;
    private BleDevice device;
    private ConnectRequest connectRequest;

    private Switch ledSwitch;
    private TextView ledText;
    private TextView buttonText;
    private EditText editText;
    private Button sendButton;

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_layout);

        ledSwitch = (Switch) findViewById(R.id.led_switch);
        ledText = (TextView) findViewById(R.id.led_text);
        buttonText = (TextView) findViewById(R.id.button_text);
        editText = (EditText) findViewById(R.id.imput_text);
        sendButton = (Button) findViewById(R.id.send_button);

        final Intent intent = getIntent();
        device = intent.getParcelableExtra(EXTRA_DEVICE);
        Log.d(TAG, "onCreate: " + device.getName() + " " + device.getAddress());
         deviceManager = new DeviceManager(getApplication(),this);
        connect();

       ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               deviceManager.turnLed(isChecked);
           }
       });

       sendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               deviceManager.Send(editText.getText().toString().trim());
           }
       });


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

    /**
     *断开连接
     */
    public void disConnect(){
        device = null;
        if (connectRequest != null){
            connectRequest.cancelPendingConnection();
        }else if (deviceManager.isConnected()){
            deviceManager.disconnect().enqueue();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disConnect();
    }

    @Override
    public void ButtonChange(String pressed) {
        buttonText.setText(pressed);
    }

    @Override
    public void LedChange(Boolean on) {
       ledText.setText(on ? "on" : "off");
       ledSwitch.setChecked(on);
    }
}