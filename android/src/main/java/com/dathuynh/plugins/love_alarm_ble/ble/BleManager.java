package com.dathuynh.plugins.love_alarm_ble.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.dathuynh.plugins.love_alarm_ble.callback.BleReadCallback;
import com.dathuynh.plugins.love_alarm_ble.callback.BleScanCallback;

import java.util.ArrayList;

public class BleManager {

    private static final String TAG = "BluetoothLE Manager";

    private final Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BleScanner bluetoothScanner;
    private BleGattClientService bleGattClientService;
    private BleGattServerService bleGattServerService;

    private String advertiseValue;

    public boolean isInitialized = false;

    public BleManager(Context context) {
        this.context = context;
        this.initialize();
    }

    public BleManager(Context context, String advertiseValue) {
        this.context = context;
        this.advertiseValue = advertiseValue;
        this.initialize();
    }

    private boolean initialize() {
        boolean hardwareSupportsBLE = context
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        if (!hardwareSupportsBLE) {
            Log.e(TAG, "BLE is not available.");
            return false;
        }

        try {
            if (bluetoothManager == null) {
                bluetoothManager = (BluetoothManager) context
                        .getSystemService(Context.BLUETOOTH_SERVICE);

                if (bluetoothManager == null) {
                    Log.e(TAG, "Unable to initialize BluetoothManager.");
                    return false;
                }
            }

            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }

            bluetoothScanner = new BleScanner(bluetoothAdapter);
            isInitialized = true;

            injectGattClientService();
            injectGattServerService();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void enable() {
        if (bluetoothAdapter == null) {
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public void scan(BleScanCallback callback) {
        this.enable();

        if (bluetoothScanner != null) {
            bluetoothScanner.start(callback);
        }
    }

    public void stopScan() {
        if (bluetoothScanner != null) {
            bluetoothScanner.stop();
        }
    }

    public void read(String address, BleReadCallback callback) {
        if (bleGattClientService == null) {
            callback.onError("BluetoothAdapter not initialized");
            return;
        }

        bleGattClientService.read(address, callback);
    }

    public void advertise() {
        if (bleGattServerService == null) {
            return;
        }
        bleGattServerService.start();
    }

    public void stopAdvertise() {
        if (bleGattServerService == null) {
            return;
        }
        bleGattServerService.stop();
    }

    public void destroy() {
        context.unbindService(gattClientServiceConnection);
        context.unbindService(gattServerServiceConnection);
        bleGattClientService = null;
        bleGattServerService = null;
    }

    private void injectGattClientService() {
        Intent gattClientServiceIntent = new Intent(context, BleGattClientService.class);
        context.bindService(
                gattClientServiceIntent,
                gattClientServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private final ServiceConnection gattClientServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleGattClientService = ((BleGattClientService.LocalBinder) service).getService();
            if (!bleGattClientService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleGattClientService = null;
        }
    };

    private void injectGattServerService() {
        Intent gattServerServiceIntent = new Intent(context, BleGattServerService.class);
        context.bindService(
                gattServerServiceIntent,
                gattServerServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private final ServiceConnection gattServerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleGattServerService = ((BleGattServerService.LocalBinder) service).getService();
            if (!bleGattServerService.initialize(advertiseValue)) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleGattServerService = null;
        }
    };
}
