package com.ela.deviceadvertisingmobile.ble.tag;

import android.bluetooth.le.ScanResult;

public class TagAngle extends Tag
{
    /** ---------- Arguments ---------- */
    private int xAng = 0;
    private int yAng = 0;
    private int zAng = 0;

    private int MSB_MAG_X = 24;
    private int LSB_MAG_X = 21;
    private int MSB_MAG_Y = 30;
    private int LSB_MAG_Y = 27;
    private int MSB_MAG_Z = 36;
    private int LSB_MAG_Z = 33;

    private final static String TAG = TagAngle.class.getSimpleName();

    /** ---------- Public functions -------- */
    public TagAngle() { }

    /**
     *   Set the data in the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void setData(ScanResult rawData, String advData)
    {
        this.name = rawData.getDevice().getName();
        this.rssi = rawData.getRssi();

        StringBuffer strX = new StringBuffer();
        StringBuffer strY = new StringBuffer();
        StringBuffer strZ = new StringBuffer();

        String tmpX = advData.substring(MSB_MAG_X,MSB_MAG_X+2).concat(advData.substring(LSB_MAG_X,LSB_MAG_X+2));
        String tmpY = advData.substring(MSB_MAG_Y,MSB_MAG_Y+2).concat(advData.substring(LSB_MAG_Y,LSB_MAG_Y+2));
        String tmpZ = advData.substring(MSB_MAG_Z,MSB_MAG_Z+2).concat(advData.substring(LSB_MAG_Z,LSB_MAG_Z+2));

        short msb = Short.parseShort(advData.substring(MSB_MAG_X,MSB_MAG_X+2),16);
        short lsb = Short.parseShort(advData.substring(LSB_MAG_X,LSB_MAG_X+2),16);
        this.xAng = (short)(lsb + (msb <<8));

        msb = Short.parseShort(advData.substring(MSB_MAG_Y,MSB_MAG_Y+2),16);
        lsb = Short.parseShort(advData.substring(LSB_MAG_Y,LSB_MAG_Y+2),16);
        this.yAng = (short)(lsb + (msb <<8));

        msb = Short.parseShort(advData.substring(MSB_MAG_Z,MSB_MAG_Z+2),16);
        lsb = Short.parseShort(advData.substring(LSB_MAG_Z,LSB_MAG_Z+2),16);
        this.zAng = (short)(lsb + (msb <<8));


    }

    /**
     *   Update the value RSSI and temp on the object
     * @param rawData [ScanResult] : Raw data
     * @param advData [String] : data convert to hex string
     */
    public void updateData(ScanResult rawData, String advData)
    {
        this.rssi = rawData.getRssi();
        StringBuffer strX = new StringBuffer();
        StringBuffer strY = new StringBuffer();
        StringBuffer strZ = new StringBuffer();

        String tmpX = advData.substring(MSB_MAG_X,MSB_MAG_X+2).concat(advData.substring(LSB_MAG_X,LSB_MAG_X+2));
        String tmpY = advData.substring(MSB_MAG_Y,MSB_MAG_Y+2).concat(advData.substring(LSB_MAG_Y,LSB_MAG_Y+2));
        String tmpZ = advData.substring(MSB_MAG_Z,MSB_MAG_Z+2).concat(advData.substring(LSB_MAG_Z,LSB_MAG_Z+2));

        short msb = Short.parseShort(advData.substring(MSB_MAG_X,MSB_MAG_X+2),16);
        short lsb = Short.parseShort(advData.substring(LSB_MAG_X,LSB_MAG_X+2),16);
        this.xAng = (short)(lsb + (msb <<8));

        msb = Short.parseShort(advData.substring(MSB_MAG_Y,MSB_MAG_Y+2),16);
        lsb = Short.parseShort(advData.substring(LSB_MAG_Y,LSB_MAG_Y+2),16);
        this.yAng = (short)(lsb + (msb <<8));

        msb = Short.parseShort(advData.substring(MSB_MAG_Z,MSB_MAG_Z+2),16);
        lsb = Short.parseShort(advData.substring(LSB_MAG_Z,LSB_MAG_Z+2),16);
        this.zAng = (short)(lsb + (msb <<8));
    }

    /**
     *   Compute 2's complement of a string
     * @param str
     * @return
     */
    private static String findTwoscomplement(StringBuffer str)
    {
        int n = str.length();

        int i;
        for (i = n-1 ; i >= 0 ; i--)
            if (str.charAt(i) == '1')
                break;

        if (i == -1)
            return "1" + str;

        for (int k = i-1 ; k >= 0; k--)
        {
            if (str.charAt(k) == '1')
                str.replace(k, k+1, "0");
            else
                str.replace(k, k+1, "1");
        }
        return str.toString();
    }

    public int getxAng() {
        return xAng;
    }

    public int getyAng() {
        return yAng;
    }

    public int getzAng() {
        return zAng;
    }
}
