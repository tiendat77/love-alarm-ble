package com.dathuynh.plugins.love_alarm_ble.ble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_client.BleGattClientService;
import com.dathuynh.plugins.love_alarm_ble.ble.ble_gatt_server.BleGattServerService;
import com.dathuynh.plugins.love_alarm_ble.callback.BleReadCallback;
import com.dathuynh.plugins.love_alarm_ble.callback.BleScanCallback;
import com.dathuynh.plugins.love_alarm_ble.callback.BleWatchCallback;

public class BleManager {

    private final Context context;

    private BleScanner scanner;
    private BleGattClientService client;
    private BleGattServerService server;

    public BleManager(Context context) {
        this.context = context;

        try {
            Bluetooth.getInstance().initialize(context);
            scanner = new BleScanner(Bluetooth.getInstance().getAdapter());

            injectGattClientService();
            injectGattServerService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enable() {
        Bluetooth.getInstance().enable();
    }

    public void startScan(BleScanCallback callback) {
        if (!Bluetooth.getInstance().isEnabled()) {
            return;
        }

        if (scanner != null) {
            scanner.start(callback);
        }
    }

    public void stopScan() {
        if (scanner != null) {
            scanner.stop();
        }
    }

    public void read(String address, BleReadCallback callback) {
        if (client == null) {
            callback.onError("BluetoothAdapter not initialized");
            return;
        }

        client.read(address, callback);
    }

    public void startAdvertise() {
        if (server == null) {
            return;
        }
        server.start();
    }

    public void stopAdvertise() {
        if (server == null) {
            return;
        }
        server.stop();
    }

    public void destroy() {
        context.unbindService(gattClientServiceConnection);
        context.unbindService(gattServerServiceConnection);
        client = null;
        server = null;
    }

    public void watch(BleWatchCallback callback) {
        server.watch(callback);
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
            client = ((BleGattClientService.LocalBinder) service).getService();
            client.initialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            client = null;
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
            server = ((BleGattServerService.LocalBinder) service).getService();
            server.initialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            server = null;
        }
    };
}
