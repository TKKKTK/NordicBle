package com.wg.nordicbletest.callback;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

public abstract class BlinkyLedDataCallback implements ProfileDataCallback, DataSentCallback,BlinkyLedCallback {
    private static final byte STATE_OFF = 0x00;
    private static final byte STATE_ON = 0x01;

    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
         parse(device,data);
    }

    @Override
    public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
        Log.d(TAG, "LEDonDataSent: "+data);
         parse(device,data);
    }

    private void parse(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }
        final int state = data.getIntValue(Data.FORMAT_UINT8, 0);
        if (state == STATE_ON) {
            onLedStateChanged(device, true);
        } else if (state == STATE_OFF) {
            onLedStateChanged(device, false);
        } else {
            onInvalidDataReceived(device, data);
        }
    }
}
