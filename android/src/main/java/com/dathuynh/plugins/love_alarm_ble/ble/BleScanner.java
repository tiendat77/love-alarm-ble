package com.dathuynh.plugins.love_alarm_ble.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.dathuynh.plugins.love_alarm_ble.callback.BleScanCallback;

import java.util.ArrayList;
import java.util.List;

public class BleScanner {

    private static final String TAG = "BluetoothLE Scanner";

    private static final long SCAN_PERIOD = 10000;

    private BleScanCallback saveBleScanCallback;
    private final BluetoothLeScanner bluetoothLeScanner;
    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();

    public boolean isScanning = false;

    public BleScanner(BluetoothAdapter bluetoothAdapter) {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Unable to obtain a BluetoothLeScanner.");
        }
    }

    public void start(BleScanCallback callback) {
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "Unable to scan.");
            callback.onResponse(false, "Unable to scan.");
            return;
        }

        if (isScanning) {
            stop();
            callback.onResponse(true, "Already scanning.");
            return;
        }

        // Do scan
        saveBleScanCallback = callback;
        devices.clear();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(
                ParcelUuid.fromString(BleProfile.PROFILE_SERVICE.toString())
        ).build());

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        bluetoothLeScanner.startScan(filters, settings, scanCallback);

        // Stop scan after a pre-defined scan period.
        new Handler().postDelayed(this::stop, SCAN_PERIOD);
    }

    public void stop() {
        isScanning = false;

        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }

        if (saveBleScanCallback != null) {
            saveBleScanCallback.onStop();
            saveBleScanCallback = null;
        }
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            if (!devices.contains(device)) {
                devices.add(device);
                saveBleScanCallback.onResult(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
}
