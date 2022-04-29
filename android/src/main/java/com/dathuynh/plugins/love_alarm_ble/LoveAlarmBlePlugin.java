package com.dathuynh.plugins.love_alarm_ble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dathuynh.plugins.love_alarm_ble.ble.BleManager;
import com.dathuynh.plugins.love_alarm_ble.callback.BleReadCallback;
import com.dathuynh.plugins.love_alarm_ble.callback.BleScanCallback;
import com.dathuynh.plugins.love_alarm_ble.utils.Conversion;
import com.dathuynh.plugins.love_alarm_ble.utils.PermissionHelper;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.util.ArrayList;

@CapacitorPlugin(
    name = "LoveAlarmBle",
    permissions = {
        @Permission(
            strings = { Manifest.permission.ACCESS_COARSE_LOCATION },
            alias = "ACCESS_COARSE_LOCATION"
        ),
        @Permission(
            strings = { Manifest.permission.ACCESS_FINE_LOCATION },
            alias = "ACCESS_FINE_LOCATION"
        ),
        @Permission(
            strings = { Manifest.permission.BLUETOOTH },
            alias = "BLUETOOTH"
        ),
        @Permission(
            strings = { Manifest.permission.BLUETOOTH_ADMIN },
            alias = "BLUETOOTH_ADMIN"
        ),
        @Permission(
            strings = { "android.permission.BLUETOOTH_SCAN" },
            alias = "BLUETOOTH_SCAN"
        ),
        @Permission(
            strings = { "android.permission.BLUETOOTH_CONNECT" },
            alias = "BLUETOOTH_CONNECT"
        ),
        @Permission(
            strings = { "android.permission.BLUETOOTH_ADVERTISE" },
            alias = "BLUETOOTH_ADVERTISE"
        )
    }
)
public class LoveAlarmBlePlugin extends Plugin {

    private String TAG = "LoveAlarmBlePlugin";

    private String userProfileId;

    private BleManager bleManager;
    private String[] aliases = new String[0];

    @PluginMethod()
    public void initialize(PluginCall call) {
        aliases = PermissionHelper.getPermissionAliases();
        userProfileId = call.getString("advertising", "Dummy user @@");
        requestPermissionForAliases(aliases, call, "checkPermission");
    }

    @PluginMethod()
    public void scan(PluginCall call) {
        BleScanCallback callback = new BleScanCallback() {
            @Override
            public void onStop() {
                Log.d(TAG, "Stopped scan.");
                call.resolve();
            }

            @Override
            public void onResponse(@NonNull Boolean success, String message) {
                Log.d(TAG, "Response " + message);
                if (!success) {
                    call.reject(message);
                } else {
                    call.resolve();
                }
            }

            @Override
            public void onResult(@NonNull BluetoothDevice device) {
                Log.d(TAG, "Result: " + device);

                JSObject result = Conversion.getScanResult(device);
                notifyListeners("onScanResult", result);
            }
        };

        bleManager.scan(callback);
    }

    @PluginMethod()
    public void stopScan(PluginCall call) {
        bleManager.stopScan();
        call.resolve();
    }

    @PluginMethod()
    public void read(PluginCall call) {
        String address = call.getString("address");
        if (address == null) {
            call.reject("Unable to read characteristic from address=null");
            return;
        }

        BleReadCallback callback = new BleReadCallback() {
            @Override
            public void onError(String message) {
                call.reject(message);
            }

            @Override
            public void onResult(String profile) {
                call.resolve(Conversion.getReadResult(address, profile));
            }
        };

        bleManager.read(address, callback);
    }

    @PluginMethod()
    public void advertise(PluginCall call) {
        bleManager.advertise();
        call.resolve();
    }

    @PluginMethod()
    public void stopAdvertise(PluginCall call) {
        bleManager.stopAdvertise();
        call.resolve();
    }

    @PermissionCallback
    private final void checkPermission(PluginCall call) {
        ArrayList<Boolean> perms = new ArrayList<>();
        for (String alias : aliases) {
            perms.add(
                getPermissionState(alias) == PermissionState.GRANTED
            );
        }

        Boolean allGranted = PermissionHelper.isAllPermissionsGranted(perms);
        if (allGranted) {
            bleManager = new BleManager(getContext(), userProfileId);
            call.resolve();
        } else {
            call.reject("Permissions denied!");
        }
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        if (bleManager != null) {
            bleManager.destroy();
        }
    }
}
