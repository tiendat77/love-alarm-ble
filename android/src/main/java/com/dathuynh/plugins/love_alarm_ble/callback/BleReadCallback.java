package com.dathuynh.plugins.love_alarm_ble.callback;

public interface BleReadCallback {
    void onError(String message);
    void onResult(String profile);
}
