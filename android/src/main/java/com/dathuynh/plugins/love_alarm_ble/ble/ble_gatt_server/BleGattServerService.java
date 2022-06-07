package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_server;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;
import com.dathuynh.plugins.love_alarm_ble.ble.Bluetooth;
import com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_client.BleClientHelper;
import com.dathuynh.plugins.love_alarm_ble.callback.BleWatchCallback;
import com.dathuynh.plugins.love_alarm_ble.notification.Notify;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BleGattServerService extends Service {

    private final static String TAG = "BleGattServerService";

    private BleServerManager manager;
    private BleWatchCallback saveBleWatchCallback;
    private BroadcastReceiver bluetoothObserver;

    public void initialize() {
        Log.d(TAG, "initialize gatt server service");

        manager = new BleServerManager(getApplication());

        if (Bluetooth.getInstance().isEnabled()) {
            start();
        }

        listenForStateChange();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothObserver);
    }

    private void listenForStateChange() {
        bluetoothObserver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        start();
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                    case BluetoothAdapter.STATE_OFF:
                        stop();
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothObserver, filter);
    }

    public void start() {
        if (manager != null) {
            manager.startAdvertise();
            manager.startServer(gattServerCallback);
        }
    }

    public void stop() {
        if (manager != null) {
            manager.stopAdvertise();
            manager.stopServer();
            manager.clearRing();
        }
    }

    public void watch(BleWatchCallback callback) {
        saveBleWatchCallback = callback;
    }

    private void onRing(String address, String profile) {
        if (saveBleWatchCallback != null) {
            saveBleWatchCallback.onResult("ring", address, profile);
        }

        manager.ring(address, profile);

        Notify.show(
                getApplicationContext(),
                "Ring ring \uD83D\uDD14\uD83D\uDD14",
                "Someone in 10m radius is ringing you!"
        );
    }

    private void onUnRing(String address) {
        if (saveBleWatchCallback != null) {
            saveBleWatchCallback.onResult("un-ring", address, "");
        }

        manager.unRing(address);
    }

    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            String address = device.getAddress();

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                onUnRing(address);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            byte[] value = BleProfile.getInstance().getId().getBytes();
            byte[] response = Arrays.copyOfRange(value, offset, value.length);

            manager.getServer().sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    offset,
                    response
            );
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            String address = device.getAddress();

            Log.d(TAG, "Write request " +
                    device + " | " +
                    characteristic.getUuid().toString() + " | " +
                    Arrays.toString(value)
            );

            if (BleClientHelper.isRingerCharacteristic(characteristic)) {
                byte[] raw = characteristic.getValue();
                if (raw != null) {
                    String profile = new String(raw, StandardCharsets.UTF_8);
                    onRing(address, profile);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stop();
        return super.onUnbind(intent);
    }

    private final IBinder binder = new BleGattServerService.LocalBinder();

    public class LocalBinder extends Binder {
        public BleGattServerService getService() {
            return BleGattServerService.this;
        }
    }
}
