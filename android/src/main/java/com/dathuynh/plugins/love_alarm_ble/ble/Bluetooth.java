package com.dathuynh.plugins.love_alarm_ble.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class Bluetooth {
    private final static String TAG = "BluetoothManager";

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private boolean isSupportLE;

    private static Bluetooth instance;

    public static Bluetooth getInstance() {
        if (instance == null) {
            instance = new Bluetooth();
        }
        return instance;
    }

    public void initialize(@NotNull Context context) {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) context
                    .getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (bluetoothManager == null) {
            Log.e(TAG, "Unable to initialize BluetoothManager.");
            return;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }

        isSupportLE = context
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public BluetoothAdapter getAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothManager getManager() {
        return this.bluetoothManager;
    }

    public boolean isSupportLE() {
        return isSupportLE;
    }

    public void enable() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    public boolean isEnabled() {
        if (bluetoothAdapter == null) {
            return false;
        }

        return bluetoothAdapter.isEnabled();
    }
}
