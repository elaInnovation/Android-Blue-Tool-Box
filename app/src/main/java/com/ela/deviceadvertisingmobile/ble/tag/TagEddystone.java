package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagEddystone extends Tag
{
    /** ---------- Arguments ---------- */

    private final static String TAG = TagEddystone.class.getSimpleName();
    public String Nid, Bid;

    /** ---------- Public functions -------- */
    public TagEddystone() { }

    /**
     *   Set the data in the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void setData(ScanResult rawData, String advData)
    {
        this.Nid = advData.substring(39,68);
        this.Nid = this.Nid.toUpperCase().replaceAll("\\s+","");
        this.Bid = advData.substring(69,86);
        this.Bid = this.Bid.toUpperCase().replaceAll("\\s+","");
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
