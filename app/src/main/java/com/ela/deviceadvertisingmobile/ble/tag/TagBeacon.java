package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagBeacon extends Tag
{
    /** ---------- Arguments ---------- */

    private final static String TAG = TagBeacon.class.getSimpleName();
    public String Uuid, Major, Minor;

    /** ---------- Public functions -------- */
    public TagBeacon() { }

    /**
     *   Set the data in the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void setData(ScanResult rawData, String advData)
    {
        this.Uuid = advData.substring(27,74);
        this.Uuid = this.Uuid.toUpperCase().replaceAll("\\s+","");
        this.Major = advData.substring(75,80);
        this.Major = this.Major.toUpperCase().replaceAll("\\s+","");
        this.Minor = advData.substring(81,86);
        this.Minor = this.Minor.toUpperCase().replaceAll("\\s+","");
        this.name = rawData.getDevice().getName();
        this.rssi = rawData.getRssi();

    }

    /**
     *   Update the value RSSI and temp on the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void updateData(ScanResult rawData, String advData)
    {
        this.rssi = rawData.getRssi();
    }
}
