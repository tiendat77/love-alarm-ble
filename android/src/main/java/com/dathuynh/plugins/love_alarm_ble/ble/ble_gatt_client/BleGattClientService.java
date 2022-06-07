package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_client;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;
import com.dathuynh.plugins.love_alarm_ble.ble.Bluetooth;
import com.dathuynh.plugins.love_alarm_ble.callback.BleReadCallback;

import java.nio.charset.StandardCharsets;

public class BleGattClientService extends Service {

    private final static String TAG = "BleGattClientService";

    private BleClientManager manager;

    private BleReadCallback saveBleReadCallback;

    public void initialize() {
        Log.d(TAG, "initialize gatt client service.");

        this.manager = new BleClientManager(getApplication());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void read(String address, BleReadCallback callback) {
        if (!Bluetooth.getInstance().isEnabled()) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            callback.onError("BluetoothAdapter not initialized");
            return;
        }

        saveBleReadCallback = callback;
        manager.connect(address, gattCallback);

        new Handler().postDelayed(this::readTimeout, 12 * 1000);
    }

    private void readTimeout() {
        if (saveBleReadCallback != null) {
            saveBleReadCallback.onResult(null);
            saveBleReadCallback = null;
        }
    }

    private void onFoundProfile(String profile, String address) {
        Log.d(TAG, "Profile ID found: " + profile);

        if (saveBleReadCallback != null) {
            saveBleReadCallback.onResult(profile);
            saveBleReadCallback = null;
        }

        Log.d(TAG, "matches: " + BleProfile.getInstance().getMatches().toString());
        boolean isMatch = BleClientHelper.isMatch(BleProfile.getInstance().getMatches(), profile);
        BluetoothGatt gatt = manager.connection((address));

        if (isMatch && gatt != null) {
            Log.d(TAG, "found matching");
            ring(gatt);
        }
    }

    private void ring(BluetoothGatt gatt) {
        String address = gatt.getDevice().getAddress();

        BluetoothGattCharacteristic characteristic = BleClientHelper.getRingerCharacteristic(
                manager.getServices(address)
        );

        if (characteristic != null) {
            Log.d(TAG, "Found Ringer Characteristic");
            byte[] value = BleProfile.getInstance().getId().getBytes();
            characteristic.setValue(value);
            gatt.writeCharacteristic(characteristic);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String address = gatt.getDevice().getAddress();

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");

                manager.connect(address, gatt);
                manager.discovery(address);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.");
                manager.close(address);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String address = gatt.getDevice().getAddress();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattCharacteristic characteristic = BleClientHelper.getProfileCharacteristic(
                        manager.getServices(address)
                );

                if (characteristic != null) {
                    // value will return in gattCallback
                    manager.read(address, characteristic);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String address = gatt.getDevice().getAddress();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] raw = characteristic.getValue();
                if (raw != null) {
                    String value = new String(raw, StandardCharsets.UTF_8);
                    onFoundProfile(value, address);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "on characteristic write" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "on characteristic changed" + characteristic.getUuid().toString());
        }
    };

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BleGattClientService getService() {
            return BleGattClientService.this;
        }
    }

}
