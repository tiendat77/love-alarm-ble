package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.dathuynh.plugins.love_alarm_ble.ble.Bluetooth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BleClientManager {
    private final static String TAG = "BleClientManager";

    private final Map<String, BluetoothGatt> connections;

    private final Context context;

    public BleClientManager(@NotNull Context context) {
        this.context = context;
        this.connections = new HashMap<>();
    }

    public void connect(final String address, BluetoothGattCallback callback) {
        if (!Bluetooth.getInstance().isSupportLE() || !Bluetooth.getInstance().isEnabled()) {
            Log.e(TAG, "Not supported bluetooth or bluetooth is disabled. Unable to connect");
            return;
        }

        if (connections.containsKey(address)) {
            BluetoothGatt connection = connections.get(address);
            connections.remove(address);

            if (connection != null) {
                connection.close();
            }
        }

        final BluetoothDevice device = Bluetooth
                .getInstance()
                .getAdapter()
                .getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found. Unable to connect.");
            return;
        }

        device.connectGatt(context, false, callback);
    }

    public void connect(String address, BluetoothGatt gatt) {
        if (connections.containsKey(address)) {
            return;
        }

        connections.put(address, gatt);
    }

    public void disconnect(String address) {
        if (Bluetooth.getInstance().getAdapter() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (connections.containsKey(address)) {
            BluetoothGatt connection = connections.get(address);

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void close(String address) {
        if (!connections.containsKey(address)) {
            return;
        }

        BluetoothGatt connection = connections.get(address);

        if (connection != null) {
            connection.close();
        }
    }

    public BluetoothGatt connection(String address) {
        return connections.get(address);
    }

    public void clear() {
        for (String address : connections.keySet()) {
            BluetoothGatt connection = connections.get(address);
            if (connection != null) {
                connection.close();
            }
            connections.remove(address);
        }
    }

    public void discovery(String address) {
        if (!connections.containsKey(address)) {
            return;
        }

        BluetoothGatt connection = connections.get(address);

        if (connection == null) {
            return;
        }

        connection.discoverServices();
    }

    public void read(String address, BluetoothGattCharacteristic characteristic) {
        if (!connections.containsKey(address)) {
            return;
        }

        BluetoothGatt connection = connections.get(address);

        if (connection == null) {
            return;
        }
        connection.readCharacteristic(characteristic);
    }

    public List<BluetoothGattService> getServices(String address) {
        if (!connections.containsKey(address)) {
            return new ArrayList<>();
        }

        BluetoothGatt connection = connections.get(address);

        if (connection == null) {
            return new ArrayList<>();
        }

        return connection.getServices();
    }
}
