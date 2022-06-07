package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_server;

import android.bluetooth.BluetoothGattCharacteristic;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;

public class BleCharacteristic {

    public static BluetoothGattCharacteristic profileCharacteristic() {
        return new BluetoothGattCharacteristic(
                // UUID
                BleProfile.PROFILE_ID,
                // Properties
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                // Permission
                BluetoothGattCharacteristic.PERMISSION_READ
        );
    }

    public static BluetoothGattCharacteristic ringerCharacteristic() {
        return new BluetoothGattCharacteristic(
                // UUID
                BleProfile.RINGER_COUNT,
                // Properties
                BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                // Permission
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE
        );
    }

}
