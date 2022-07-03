package com.wg.nordicbletest.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BlinkyLedCallback {

    /**
     * Called when the data has been sent to the connected device.
     *当数据被发送到连接的设备时调用。
     * @param device the target device.
     * @param on true when LED was enabled, false when disabled.
     */
    void onLedStateChanged(@NonNull final BluetoothDevice device, final boolean on);
}
