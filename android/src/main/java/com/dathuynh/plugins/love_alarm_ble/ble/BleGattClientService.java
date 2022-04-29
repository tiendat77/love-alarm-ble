package com.dathuynh.plugins.love_alarm_ble.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dathuynh.plugins.love_alarm_ble.Profile;
import com.dathuynh.plugins.love_alarm_ble.callback.BleReadCallback;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class BleGattClientService extends Service {

    private final static String TAG = "BleGattClientService";

    public final static String ACTION_GATT_CONNECTED = "com.dathuynh.plugins.love_alarm_ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.dathuynh.plugins.love_alarm_ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.dathuynh.plugins.love_alarm_ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.dathuynh.plugins.love_alarm_ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.dathuynh.plugins.love_alarm_ble.EXTRA_DATA";

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    private String saveBleAddress;
    private BleReadCallback saveBleReadCallback;

    public boolean initialize() {
        Log.e(TAG, "initialize service.");
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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

        return true;
    }

    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        Log.d(TAG, "Trying to create a new connection.");
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        return true;
    }

    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    public void discovery() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.discoverServices();
    }

    public void read(String address, BleReadCallback callback) {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            callback.onError("BluetoothAdapter not initialized");
            return;
        }

        saveBleAddress = address;
        saveBleReadCallback = callback;
        connect(address);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                readTimeout();
            }
        }, 12 * 1000);
    }

    private void readTimeout() {
        if (saveBleReadCallback != null) {
            saveBleReadCallback.onResult(null);
            saveBleReadCallback = null;
            saveBleAddress = null;
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void findProfileCharacteristic() {
        List<BluetoothGattService> services = getSupportedGattServices();

        BluetoothGattCharacteristic profileCharacteristic = null;

        for (BluetoothGattService service : services) {
            String serviceUuid = service.getUuid().toString();
            if (Profile.PROFILE_SERVICE.toString().equals(serviceUuid)) {
                Log.d(TAG, "Found Love Alarm service: " + serviceUuid);
                profileCharacteristic = service.getCharacteristic(Profile.PROFILE_ID);
                break;
            }
        }

        if (profileCharacteristic != null) {
            // value will return in gattCallback
            bluetoothGatt.readCharacteristic(profileCharacteristic);
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                discovery();
                Log.i(TAG, "Connected to GATT server.");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                findProfileCharacteristic();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] raw = characteristic.getValue();
                if (raw != null) {
                    String value = new String(raw, StandardCharsets.UTF_8);
                    Log.d(TAG, "Profile ID found: " + value);

                    if (saveBleReadCallback != null) {
                        saveBleReadCallback.onResult(value);
                        saveBleReadCallback = null;
                    }
                }
            }
        }
    };

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BleGattClientService getService() {
            return BleGattClientService.this;
        }
    }
}
