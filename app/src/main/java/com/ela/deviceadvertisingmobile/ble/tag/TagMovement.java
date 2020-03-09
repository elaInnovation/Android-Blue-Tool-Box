package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagMovement extends Tag
{
    /** ---------- Arguments ---------- */
    private int counter = 0;
    private boolean state = false;

    private int MSB_TEMP = 24;
    private int LSB_TEMP = 21;

    private final static String TAG = TagMovement.class.getSimpleName();

    /** ---------- Public functions -------- */
    public TagMovement() { }

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

        int a = Integer.parseInt(temp, 16);
        String bin = Integer.toBinaryString(a);
        String state = bin.substring(bin.length()-1);
        if(state == "0")
        {
            this.state = false;
        }
        else if(state == "1")
        {
            this.state = true;
        }

        String tmpCount = bin.substring(0,bin.length()-1);
        if(tmpCount.length() > 1)
            this.counter = Integer.parseInt(tmpCount,2);
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

        int a = Integer.parseInt(temp, 16);
        String bin = Integer.toBinaryString(a);
        String state = bin.substring(bin.length()-1);
        if(state.equals("0"))
        {
            this.state = false;
        }
        else if(state.equals("1"))
        {
            this.state = true;
        }

        String tmpCount = bin.substring(0,bin.length()-1);
        if(tmpCount.equals(""))
        {
            return;
        }
        this.counter = Integer.parseInt(tmpCount,2);

    }

    /**
     *   Get the state value
     * @return state of the tag
     */
    public boolean getState()
    {
        return this.state;
    }

    /**
     *   Get the counter value
     * @return counter of the tag
     */
    public int getCounter()
    {
        return this.counter;
    }

}
