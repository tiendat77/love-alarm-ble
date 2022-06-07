package com.dathuynh.plugins.love_alarm_ble.ble;

import java.util.ArrayList;
import java.util.UUID;

public class BleProfile {
    public static UUID PROFILE_SERVICE = UUID.fromString("000062d9-0000-1000-8000-00805f9b34fb");
    public static UUID PROFILE_ID = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static UUID RINGER_COUNT = UUID.fromString("59d55938-0000-1000-8000-00805f9b34fb");

    private static BleProfile instance;

    private String id;
    private final ArrayList<String> matches = new ArrayList<>();

    public static BleProfile getInstance() {
        if (instance == null) {
            instance = new BleProfile();
        }
        return instance;
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<String> matches) {
        this.matches.clear();
        this.matches.addAll(matches);
    }
}
