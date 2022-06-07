package com.dathuynh.plugins.love_alarm_ble.callback;

import androidx.annotation.NonNull;

public interface BleWatchCallback {
    void onResult(@NonNull String type, @NonNull String address, @NonNull String profile);
}
