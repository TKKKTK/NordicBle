package com.wg.nordicbletest.callback;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class BlinkyButtonDataCallback implements ProfileDataCallback,BlinkyButtonCallback {
    private static final int STATE_RELEASED = 0x00;
    private static final int STATE_PRESSED = 0x01;

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }
        Log.d(TAG, "onDataReceived: "+data.toString());
        final int state = data.getIntValue(Data.FORMAT_UINT8, 0);
        if (state == STATE_PRESSED) {
            onButtonStateChanged(device, true);
        } else if (state == STATE_RELEASED) {
            onButtonStateChanged(device, false);
        } else {
            onInvalidDataReceived(device, data);
        }
    }

}
