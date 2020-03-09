package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public abstract class Tag
{
    /** ---------- Arguments ---------- */
    protected String name;
    protected int rssi;


    /** ---------- Public functions -------- */
    public String getName()
    {
        return this.name;
    }

    public int getRssi()
    {
        return this.rssi;
    }

    public Tag getTag()
    {
        return this;
    }
}
