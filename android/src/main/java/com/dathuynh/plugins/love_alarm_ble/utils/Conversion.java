package com.dathuynh.plugins.love_alarm_ble.utils;

import android.bluetooth.BluetoothDevice;

import com.getcapacitor.JSObject;

public class Conversion {

    /*
        export interface ScanResult {
            address: any;
            name?: string;
        }
    * */
    public static JSObject getScanResult(BluetoothDevice device) {
        JSObject result = new JSObject();
        result.put("address", device.getAddress());
        result.put("name", device.getName());
        return result;
    }

    /*
        export interface ReadResult {
          address: string;
          name?: string;
          profile?: string;
        }
    * */
    public static JSObject getReadResult(String address, String profile) {
        JSObject result = new JSObject();
        result.put("address", address);
        result.put("name", null);
        result.put("profile", profile);
        return result;
    }

    /*
        export interface WatchResult {
          type: 'ring' | 'un-ring';
          address?: string;
          profile?: string;
        }
    * */
    public static JSObject getWatchResult(String type, String address, String profile) {
        JSObject result = new JSObject();
        result.put("type", type);
        result.put("address", address);
        result.put("profile", profile);
        return result;
    }

}
