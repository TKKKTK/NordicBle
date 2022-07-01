package com.wg.nordicbletest;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.livedata.ObservableBleManager;

public class DeviceManager extends ObservableBleManager {

    /** Nordic Blinky Service UUID. */
    public final static UUID LBS_UUID_SERVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    /** BUTTON characteristic UUID. */
    private final static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    /** LED characteristic UUID. */
    private final static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123");

    private BluetoothGattCharacteristic buttonCharacteristic,ledCharacteristic;
    private boolean supported;
    private boolean ledOn;

    public DeviceManager(@NonNull Context context) {
        super(context);

    }

    public DeviceManager(@NonNull Context context, @NonNull Handler handler) {
        super(context, handler);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new DeviceBleManagerGattCallback();
    }

    private class DeviceBleManagerGattCallback extends BleManagerGattCallback{

        @Override
        protected void initialize() {
            super.initialize();

        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            return true;
        }

        @Override
        protected void onServicesInvalidated() {

        }
    }


}
