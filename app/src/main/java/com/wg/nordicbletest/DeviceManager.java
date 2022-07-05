package com.wg.nordicbletest;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wg.nordicbletest.callback.BlinkyButtonDataCallback;
import com.wg.nordicbletest.callback.BlinkyLedDataCallback;
import com.wg.nordicbletest.callback.TwowaytoDataCallback;
import com.wg.nordicbletest.data.BlinkyLED;
import com.wg.nordicbletest.utils.TypeConversion;

import java.util.UUID;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;

public class DeviceManager extends ObservableBleManager {

    /** Nordic Blinky Service UUID. */
    public final static UUID LBS_UUID_SERVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    /** BUTTON characteristic UUID. */
    private final static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    /** LED characteristic UUID. */
    private final static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123");

    //bt_patch(mtu).bin
    public static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");  //蓝牙通讯服务
    public static final UUID READ_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");  //读特征
    public static final UUID WRITE_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");  //写特征 //服务

    private BluetoothGattCharacteristic readCharacter,writeCharacter;
    private BluetoothGattCharacteristic buttonCharacteristic,ledCharacteristic;
    private boolean supported;
    private boolean ledOn;

    private UIChangeListener uiChangeListener;

    public DeviceManager(@NonNull Context context,UIChangeListener listener) {
        super(context);
        uiChangeListener = listener;
    }

    public DeviceManager(@NonNull Context context, @NonNull Handler handler) {
        super(context, handler);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new DeviceBleManagerGattCallback();
    }


   private final BlinkyButtonDataCallback buttonDataCallback = new BlinkyButtonDataCallback() {
       @Override
       public void onButtonStateChanged(@NonNull BluetoothDevice device, boolean pressed) {
             uiChangeListener.ButtonChange(pressed ? "pressed" : "released");
       }

       @Override
       public void onInvalidDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
           super.onInvalidDataReceived(device, data);
       }
   };

    private final BlinkyLedDataCallback ledDataCallback = new BlinkyLedDataCallback() {
        @Override
        public void onLedStateChanged(@NonNull BluetoothDevice device, boolean on) {
            ledOn = on;
            uiChangeListener.LedChange(on);
        }

        @Override
        public void onInvalidDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            super.onInvalidDataReceived(device, data);
        }
    };

    private final TwowaytoDataCallback twowaytoDataCallback = new TwowaytoDataCallback() {
        @Override
        public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
            super.onDataSent(device, data);
            Log.d(TAG, "onDataSent: "+data);
        }

        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            super.onDataReceived(device, data);
            Log.d(TAG, "onDataReceived: "+data);
        }
    };


    private class DeviceBleManagerGattCallback extends BleManagerGattCallback{

        @Override
        protected void initialize() {
            super.initialize();

             setNotificationCallback(buttonCharacteristic).with(buttonDataCallback);
             readCharacteristic(ledCharacteristic).with(ledDataCallback).enqueue();
             readCharacteristic(buttonCharacteristic).with(buttonDataCallback).enqueue();
             enableNotifications(buttonCharacteristic).enqueue();
             setNotificationCallback(readCharacter).with(twowaytoDataCallback);
             readCharacteristic(readCharacter).with(twowaytoDataCallback).enqueue();
             enableNotifications(readCharacter).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(LBS_UUID_SERVICE);
            final BluetoothGattService twtService = gatt.getService(SERVICE_UUID);
            if (service != null) {
                buttonCharacteristic = service.getCharacteristic(LBS_UUID_BUTTON_CHAR);
                ledCharacteristic = service.getCharacteristic(LBS_UUID_LED_CHAR);

                boolean writeRequest = false;
                if (ledCharacteristic != null) {
                    final int ledProperties = ledCharacteristic.getProperties();
                    writeRequest = (ledProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                }

                supported = buttonCharacteristic != null && ledCharacteristic != null && writeRequest;
            }else if (twtService != null){
                readCharacter = twtService.getCharacteristic(READ_UUID);
                writeCharacter = twtService.getCharacteristic(WRITE_UUID);

                boolean twtWriteRequest = false;
                if (writeCharacter != null){
                    final int writeProperties = writeCharacter.getProperties();
                    twtWriteRequest = (writeProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                }

                supported = readCharacter != null && writeCharacter != null && twtWriteRequest;
            }

            return supported;
        }

        @Override
        protected void onServicesInvalidated() {
            buttonCharacteristic = null;
            ledCharacteristic = null;
            readCharacter = null;
            writeCharacter = null;
        }
    }

    /**
     * Sends a request to the device to turn the LED on or off.
     *
     * @param on true to turn the LED on, false to turn it off.
     */
    public void turnLed(final boolean on) {
        // Are we connected?
        if (ledCharacteristic == null)
            return;

        // No need to change?
        if (ledOn == on)
            return;

        log(Log.VERBOSE, "Turning LED " + (on ? "ON" : "OFF") + "...");
        writeCharacteristic(
                ledCharacteristic,
                BlinkyLED.turn(on),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).with(ledDataCallback).enqueue();
    }

    public void Send(String hexString){
          Data data = new Data(TypeConversion.hexString2Bytes(hexString));
          writeCharacteristic(writeCharacter,
                  data,
                  BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                  ).with(twowaytoDataCallback).enqueue();
    }

}
