package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagTemperature extends Tag
{
    /** ---------- Arguments ---------- */
    private double temperature = 0.00;

    private int MSB_TEMP = 24;
    private int LSB_TEMP = 21;

    private final static String TAG = TagTemperature.class.getSimpleName();

    /** ---------- Public functions -------- */
    public TagTemperature() { }

    /**
     *   Set the data in the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void setData(ScanResult rawData, String advData)
    {
        this.name = rawData.getDevice().getName();
        this.rssi = rawData.getRssi();
        String temp = advData.substring(MSB_TEMP,MSB_TEMP+2).concat(advData.substring(LSB_TEMP,LSB_TEMP+2));
        int decimal=Integer.parseInt(temp,16);
        this.temperature = (double)decimal / 100;
    }

    /**
     *   Update the value RSSI and temp on the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void updateData(ScanResult rawData, String advData)
    {
        this.rssi = rawData.getRssi();
        String temp = advData.substring(MSB_TEMP,MSB_TEMP+2).concat(advData.substring(LSB_TEMP,LSB_TEMP+2));
        int decimal=Integer.parseInt(temp,16);
        this.temperature = (double)decimal / 100;
    }

    /**
     *   Get the temperature value
     * @return temperature of the tag
     */
    public double getTemperature()
    {
        return this.temperature;
    }

}
