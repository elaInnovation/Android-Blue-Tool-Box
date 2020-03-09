package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagId extends Tag
{
    /** ---------- Arguments ---------- */

    private final static String TAG = TagId.class.getSimpleName();

    /** ---------- Public functions -------- */
    public TagId() { }

    /**
     *   Set the data in the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void setData(ScanResult rawData, String advData)
    {
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
