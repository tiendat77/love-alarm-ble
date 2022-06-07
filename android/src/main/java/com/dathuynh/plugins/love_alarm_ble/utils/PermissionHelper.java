package com.dathuynh.plugins.love_alarm_ble.utils;

import android.os.Build;

import java.util.ArrayList;

public class PermissionHelper {

    public static String[] getPermissionAliases() {
        String[] aliases;

        if (Build.VERSION.SDK_INT >= 31) {
            aliases = new String[] {
                    "BLUETOOTH_SCAN",
                    "BLUETOOTH_CONNECT",
                    "BLUETOOTH_ADVERTISE",
                    "ACCESS_FINE_LOCATION"
            };
        } else {
            aliases = new String[] {
                    "ACCESS_COARSE_LOCATION",
                    "ACCESS_FINE_LOCATION",
                    "BLUETOOTH",
                    "BLUETOOTH_ADMIN"
            };
        }

        return aliases;
    }

    public static Boolean isAllPermissionsGranted(ArrayList<Boolean> permissions) {
        if (permissions.size() == 0) {
            return true;
        }

        boolean allGranted = true;

        for (Boolean permission : permissions) {
            if (!permission) {
                allGranted = false;
                break;
            }
        }

        return allGranted;
    }

}
