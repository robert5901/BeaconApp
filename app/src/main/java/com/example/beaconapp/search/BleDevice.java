package com.example.beaconapp.search;

import android.os.ParcelUuid;

import java.util.List;

public class BleDevice {
    String mac;
    String rssi;

    public BleDevice(String mac, String rssi) {
        this.mac = mac;
        this.rssi = rssi;
    }
}
