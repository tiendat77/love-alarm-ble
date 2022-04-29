package com.dathuynh.plugins.love_alarm_ble.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BleScanCallback {
    void onStop();
    void onResponse(@NonNull Boolean success, String message);
    void onResult(@NonNull BluetoothDevice device);
}
