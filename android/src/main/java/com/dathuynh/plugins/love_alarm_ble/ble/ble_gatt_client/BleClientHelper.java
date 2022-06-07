package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_client;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;

import java.util.List;

public class BleClientHelper {

    public static BluetoothGattCharacteristic getProfileCharacteristic(List<BluetoothGattService> services) {
        BluetoothGattCharacteristic characteristic = null;

        for (BluetoothGattService service : services) {
            String serviceUuid = service.getUuid().toString();

            if (BleProfile.PROFILE_SERVICE.toString().equals(serviceUuid)) {
                characteristic = service.getCharacteristic(BleProfile.PROFILE_ID);
                break;
            }
        }

        return characteristic;
    }

    public static BluetoothGattCharacteristic getRingerCharacteristic(List<BluetoothGattService> services) {
        BluetoothGattCharacteristic characteristic = null;

        for (BluetoothGattService service : services) {
            String serviceUuid = service.getUuid().toString();

            if (BleProfile.PROFILE_SERVICE.toString().equals(serviceUuid)) {
                characteristic = service.getCharacteristic(BleProfile.RINGER_COUNT);
                break;
            }
        }

        return characteristic;
    }

    public static boolean isRingerCharacteristic(BluetoothGattCharacteristic characteristic) {
        String ringerUuid = characteristic.getUuid().toString();
        return BleProfile.RINGER_COUNT.toString().equals(ringerUuid);
    }

    public static boolean isMatch(List<String> profiles, String profile) {
        if (profiles == null || profile == null) {
            return false;
        }
        return profiles.contains(profile);
    }

}
