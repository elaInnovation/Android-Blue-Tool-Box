package com.ela.deviceadvertisingmobile.ble;

import android.bluetooth.le.ScanResult;

import com.ela.deviceadvertisingmobile.ble.tag.Tag;
import com.ela.deviceadvertisingmobile.ble.tag.TagAngle;
import com.ela.deviceadvertisingmobile.ble.tag.TagId;
import com.ela.deviceadvertisingmobile.ble.tag.TagMagnetic;
import com.ela.deviceadvertisingmobile.ble.tag.TagMovement;
import com.ela.deviceadvertisingmobile.ble.tag.TagTempHumi;
import com.ela.deviceadvertisingmobile.ble.tag.TagTemperature;
import java.util.LinkedHashMap;


public class BleFactory
{
    /** ---------- Arguments ---------- */
    private static BleFactory instance = null;
    private LinkedHashMap<String, Tag> tagList = new LinkedHashMap<>();
    private Tag currentTag;
    private LinkedHashMap<String, String> macList = new LinkedHashMap<>();

    private String CARAC_T_1 = "6e";
    private String CARAC_T_2 = "2a";
    private String TYPE_NAME_T = "09";
    private String CARAC_RH_1 = "6f";
    private String CARAC_RH_2 = "2a";
    private String TYPE_NAME_RHT = "16";
    private String CARAC_M_1 = "06";
    private String CARAC_M_2 = "2a";
    private String CARAC_M_3 = "3f";
    private String CARAC_M_4 = "2a";
    private String CARAC_M_5 = "01";
    private String TYPE_NAME_MOV = "16";
    private String CARAC_ID = "09";
    private String CARAC_ANG_1 = "a1";
    private String CARAC_ANG_2 = "2a";

    private int FIRST_CARAC = 15;
    private int SECOND_CARAC = 18;
    private int THIRD_CARAC = 33;
    private int FOURTH_CARAC = 36;
    private int NAME_TYPE = 30;
    private int DATA_CARAC = 39;
    private int ID_CARAC_1 = 12;

    /** ---------- Public functions -------- */
    public BleFactory() { }

    /**
     *   singleton on BleFactory
     * @return Instance
     */
    public static BleFactory getInstance()
    {
        if(instance == null)
            instance = new BleFactory();
        return instance;
    }

    /**
     *   clear the factory on each start of a fragment
     */
    public void clearFactory()
    {
        this.tagList.clear();
        this.macList.clear();
        this.currentTag = null;
    }

    /**
     *   Set the config in the tag object
     * @param rawData [ScanResult] : Raw data
     */
    public void setTag(ScanResult rawData)
    {
        if(rawData.getScanRecord() == null) { return; }

        String data = bytesToHex(rawData.getScanRecord().getBytes());

        if(!this.tagList.containsKey(rawData.getDevice().getName()))
        {
            if(isTempHumidity(data))
            {
                TagTempHumi tag = new TagTempHumi();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
            else if (isTemperature(data))
            {
                TagTemperature tag = new TagTemperature();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
            else if (isMovement(data))
            {
                TagMovement tag = new TagMovement();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
            else if(isId(data) || isBeacon(data) || isEddystone(data))
            {
                TagId tag = new TagId();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
            else if(isAng(data))
            {
                TagAngle tag = new TagAngle();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
            else if(isMagnetic(data))
            {
                TagMagnetic tag = new TagMagnetic();
                tag.setData(rawData, data);
                if(tag.getName() == null) { return; }
                this.currentTag = tag;
                this.tagList.put(rawData.getDevice().getName(), tag);
                this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            }
        }
        else
        {
            if(rawData.getDevice().getName() == null) { return; }
            if(this.tagList.get(rawData.getDevice().getName()) == null) { return; }

            updateList(this.tagList.get(rawData.getDevice().getName()),rawData,data);
        }
    }

    /**
     *   Get the tag list
     * @return Tag List
     */
    public LinkedHashMap<String, Tag> getTagList()
    {
        return this.tagList;
    }


    /**
     *   Get the tag list mac
     * @return MAC list
     */
    public LinkedHashMap<String, String> getMacTagList()
    {
        return this.macList;
    }


    /**
     *   Get the current tag
     * @return [Tag] : current tag scanned
     */
    public Tag getCurrentTag()
    {
        return this.currentTag;
    }

    public void clearCurrentTag() { this.currentTag = null; }

    /** ---------- Private functions -------- */
    /**
     *   Update the current list of tags
     * @param tag [Tag] : Tag object from the list
     * @param rawData [ScanResult] : Raw data
     * @param data [String] : data convert to hex string
     */
    private void updateList(Tag tag, ScanResult rawData, String data)
    {
        this.tagList.remove(tag.getName());

        if(tag instanceof TagTempHumi)
        {
            TagTempHumi tagRHT = (TagTempHumi) tag;
            tagRHT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagRHT;
        }

        else if(tag instanceof TagTemperature)
        {
            TagTemperature tagT = (TagTemperature) tag;
            tagT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagT;
        }
        else if(tag instanceof TagMovement)
        {
            TagMovement tagT = (TagMovement) tag;
            tagT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagT;
        }
        else if(tag instanceof TagId)
        {
            TagId tagT = (TagId) tag;
            tagT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagT;
        }
        else if (tag instanceof TagAngle)
        {
            TagAngle tagT = (TagAngle) tag;
            tagT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagT;
        }
        else if (tag instanceof TagMagnetic)
        {
            TagMagnetic tagT = (TagMagnetic) tag;
            tagT.updateData(rawData, data);
            this.tagList.put(rawData.getDevice().getName(), tag);
            this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());
            this.currentTag = tagT;
        }
    }

    /**
     *   Check if the Scan Result is a temperature tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isTemperature(String data)
    {
        if((data.substring(FIRST_CARAC,FIRST_CARAC+2).equals(CARAC_T_1)) &&
                (data.substring(SECOND_CARAC,SECOND_CARAC+2).equals(CARAC_T_2)) &&
                (data.substring(NAME_TYPE,NAME_TYPE+2).equals(TYPE_NAME_T)))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is a RHT tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isTempHumidity(String data)
    {
        if((data.substring(FIRST_CARAC,FIRST_CARAC+2).equals(CARAC_T_1)) &&
                (data.substring(SECOND_CARAC,SECOND_CARAC+2).equals(CARAC_T_2)) &&
                (data.substring(NAME_TYPE,NAME_TYPE+2).equals(TYPE_NAME_RHT)) &&
                (data.substring(THIRD_CARAC,THIRD_CARAC+2).equals(CARAC_RH_1)) &&
                (data.substring(FOURTH_CARAC,FOURTH_CARAC+2).equals(CARAC_RH_2)))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is a MOV tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isMovement(String data)
    {
        if((data.substring(FIRST_CARAC,FIRST_CARAC+2).equals(CARAC_M_1)) &&
                (data.substring(SECOND_CARAC,SECOND_CARAC+2).equals(CARAC_M_2)) &&
                (data.substring(NAME_TYPE,NAME_TYPE+2).equals(TYPE_NAME_MOV)) &&
                (data.substring(THIRD_CARAC,THIRD_CARAC+2).equals(CARAC_M_3)) &&
                (data.substring(FOURTH_CARAC,FOURTH_CARAC+2).equals(CARAC_M_4)) &&
                (data.substring(DATA_CARAC,DATA_CARAC+2).equals(CARAC_M_5)))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is an IDs tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isId(String data)
    {
        if(data.substring(ID_CARAC_1,ID_CARAC_1+2).equals(CARAC_ID))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is an iBeacon tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isBeacon(String data)
    {
        if(data.substring(ID_CARAC_1,ID_CARAC_1+2).equals("ff"))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is an Eddystone tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isEddystone(String data)
    {
        if(data.substring(ID_CARAC_1,ID_CARAC_1+2).equals("03"))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is an MAG tag
     * @param data
     * @return
     */
    private boolean isAng(String data)
    {
        if((data.substring(FIRST_CARAC,FIRST_CARAC+2).equals(CARAC_ANG_1)) &&
                (data.substring(SECOND_CARAC,SECOND_CARAC+2).equals(CARAC_ANG_2)))
        {
            return true;
        }
        return false;
    }

    /**
     *   Check if the Scan Result is a MOV tag
     * @param data [String] : data in hex as string
     * @return [boolean]
     */
    private boolean isMagnetic(String data)
    {
        if((data.substring(FIRST_CARAC,FIRST_CARAC+2).equals(CARAC_M_1)) &&
                (data.substring(SECOND_CARAC,SECOND_CARAC+2).equals(CARAC_M_2)) &&
                (data.substring(NAME_TYPE,NAME_TYPE+2).equals(TYPE_NAME_MOV)) &&
                (data.substring(THIRD_CARAC,THIRD_CARAC+2).equals(CARAC_M_3)) &&
                (data.substring(FOURTH_CARAC,FOURTH_CARAC+2).equals(CARAC_M_4)) &&
                (data.substring(DATA_CARAC,DATA_CARAC+2).equals("00")))
        {
            return true;
        }
        return false;
    }

    /**
     *   Convert bytes to hex string
     * @param hashInBytes
     * @return [String] : data in a string list
     */
    private static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x ", b));
        }
        return sb.toString();
    }

}
