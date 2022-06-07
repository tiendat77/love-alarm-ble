package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_server;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.os.ParcelUuid;
import android.util.Log;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;

public class BleAdvertiser {

    private static AdvertiseCallback _callback;

    public static AdvertiseSettings settings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
    }

    public static AdvertiseData data() {
        return new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(BleProfile.PROFILE_SERVICE))
                .build();
    }

    public static AdvertiseCallback callback() {
        if (_callback == null) {
            _callback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Log.i("GattAdvertise", "LE Advertise Started");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.w("GattAdvertise", "LE Advertise Failed:" + errorCode);
                }
            };
        }

        return _callback;
    }
}
