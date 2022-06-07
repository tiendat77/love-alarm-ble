package com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_server;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.util.Log;

import com.dathuynh.plugins.love_alarm_ble.ble.BleProfile;
import com.dathuynh.plugins.love_alarm_ble.ble.Bluetooth;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BleServerManager {
    private final static String TAG = "BleServerManager";

    private final Map<String, String> ringers;
    private BluetoothGattServer gattServer;
    private BluetoothLeAdvertiser advertiser;

    private final Context context;

    public BleServerManager(@NotNull Context context) {
        this.context = context;
        this.ringers = new HashMap<>();
    }

    /**
     * Gatt Server
     */
    public void startServer(BluetoothGattServerCallback gattServerCallback) {
        gattServer = Bluetooth.getInstance()
                .getManager()
                .openGattServer(context, gattServerCallback);

        if (gattServer == null) {
            Log.e(TAG, "Unable to create GATT server");
            return;
        }

        BluetoothGattService service = new BluetoothGattService(
                BleProfile.PROFILE_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
        );

        BluetoothGattCharacteristic profile = BleCharacteristic.profileCharacteristic();
        profile.setValue(
                BleProfile.getInstance()
                        .getId()
                        .getBytes(StandardCharsets.UTF_8)
        );

        BluetoothGattCharacteristic ringer = BleCharacteristic.ringerCharacteristic();

        service.addCharacteristic(profile);
        service.addCharacteristic(ringer);
        gattServer.addService(service);
    }

    public void stopServer() {
        if (gattServer == null) {
            return;
        }

        gattServer.close();
        gattServer = null;
    }

    /**
     * Advertise
     */
    public void startAdvertise() {
        if (Bluetooth.getInstance().getAdapter() == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        advertiser = Bluetooth.getInstance().getAdapter().getBluetoothLeAdvertiser();

        if (advertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        advertiser.startAdvertising(
                BleAdvertiser.settings(),
                BleAdvertiser.data(),
                BleAdvertiser.callback()
        );
    }

    public void stopAdvertise() {
        if (advertiser == null) {
            return;
        }

        advertiser.stopAdvertising(BleAdvertiser.callback());
        advertiser = null;
    }

    /**
     * Communicate
     */
    public BluetoothGattServer getServer() {
        return gattServer;
    }

    /**
     * Ringers
     */
    public void ring(String address, String profile) {
        if (ringers.containsKey(address)) {
            return;
        }

        ringers.put(address, profile);
    }

    public void unRing(String address) {
        if (!ringers.containsKey(address)) {
            return;
        }

        ringers.remove(address);
    }

    public void clearRing() {
        ringers.clear();
    }

    public int countRing() {
        return ringers.size();
    }
}
