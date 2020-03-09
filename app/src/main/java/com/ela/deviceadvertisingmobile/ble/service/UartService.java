package com.ela.deviceadvertisingmobile.ble.service;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ela.deviceadvertisingmobile.ble.BleManager;
import com.ela.deviceadvertisingmobile.ble.BleUtils;
import com.ela.deviceadvertisingmobile.view.DebugText;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

public class UartService implements BleManager.BleManagerListener {

    // Log
    private final static String TAG = UartService.class.getSimpleName();

    // Service Constants
    public static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_RX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_DFU = "00001530-1212-EFDE-1523-785FEABCD123";
    public static final int kTxMaxCharacters = 20;

    /** constructor using instance of BLE manager
     * @param instance instance : insatnce of BLE manager
     */
    public UartService(BleManager instance)
    {
        this.mBleManager = instance;
    }

    /** BLE manager instance */
    protected BleManager mBleManager;

    /** instance of uart service */
    protected BluetoothGattService mUartService;

    /** RX notication enable or not */
    private boolean isRxNotificationEnabled = false;

    /** send data handler timeout*/
    final private Handler sendDataTimeoutHandler = new Handler();
    private Runnable sendDataRunnable = null;
    private SendDataCompletionHandler sendDataCompletionHandler = null;

    /**********************************************************************************************/
    /** region Send Data to UART
     * @param text text : command to send using the uart nordic service
     */
    public boolean sendData(String text) {
        final byte[] value = text.getBytes(Charset.forName("UTF-8"));
        return sendData(value);
    }

    /** function to send data
     * @param data data : byte data to send
     */
    public boolean sendData(byte[] data) {
        if (mUartService != null) {
            // Split the value into chunks (UART service has a maximum number of characters that can be written )
            for (int i = 0; i < data.length; i += kTxMaxCharacters) {
                final byte[] chunk = Arrays.copyOfRange(data, i, Math.min(i + kTxMaxCharacters, data.length));
                try
                {
                    mBleManager.writeService(mUartService, UUID_TX, chunk);
                } catch (Exception e)
                {
                    System.out.println(e.getLocalizedMessage());
                }

            }
            return true;
        } else {
            DebugText.getInstance().write(">>> ERROR : Uart Service not discovered. Unable to send data");
            Log.w(TAG, "Uart Service not discovered. Unable to send data");
            return false;
        }
    }

    /** Send data to UART and add a byte with a custom CRC
     * @param data data : byte data to send
     */
    public void sendDataWithCRC(byte[] data) {

        // Calculate checksum
        byte checksum = 0;
        for (byte aData : data) {
            checksum += aData;
        }
        checksum = (byte) (~checksum);       // Invert

        // Add crc to data
        byte dataCrc[] = new byte[data.length + 1];
        System.arraycopy(data, 0, dataCrc, 0, data.length);
        dataCrc[data.length] = checksum;

        // Send it
        Log.d(TAG, "Send to UART: " + BleUtils.bytesToHexWithSpaces(dataCrc));
        sendData(dataCrc);
    }
    // endregion

    /** region SendDataWithCompletionHandler */
    public interface SendDataCompletionHandler {
        void sendDataResponse(String data);
    }

    /** send data through the service
     * @param data data : byte data to send
     * @param completionHandler : completion handler, called when the function is finished
     */
    public void sendData(byte[] data, SendDataCompletionHandler completionHandler) {

        if (completionHandler == null) {
            sendData(data);
            return;
        }

        if (!isRxNotificationEnabled) {
            Log.w(TAG, "sendData warning: RX notification not enabled. completionHandler will not be executed");
        }

        if (sendDataRunnable != null || sendDataCompletionHandler != null) {
            Log.d(TAG, "sendData error: waiting for a previous response");
            return;
        }

        Log.d(TAG, "sendData");
        sendDataCompletionHandler = completionHandler;
        sendDataRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sendData timeout");
                final SendDataCompletionHandler dataCompletionHandler = sendDataCompletionHandler;

                /*
                UartInterfaceActivity.this.sendDataRunnable = null;
                UartInterfaceActivity.this.sendDataCompletionHandler = null;
                */

                dataCompletionHandler.sendDataResponse(null);
            }
        };

        sendDataTimeoutHandler.postDelayed(sendDataRunnable, 2 * 1000);
        sendData(data);

    }

    /** function to known if a data response is expedted
     * return true if waiting a data respon, false if not
     */
    public boolean isWaitingForSendDataResponse() {

        return sendDataRunnable != null;
    }

    /** enable receive Notifications, used for bluetooth notiication */
    public void enableRxNotifications() {
        isRxNotificationEnabled = true;
        mBleManager.enableNotification(mUartService, UUID_RX, true);
    }

    /**
     *   Read the data in the Rx service
     * @return byte read on the characteristic
     */
    public byte[] readRxMessage()
    {
        while(mUartService.getCharacteristics().get(1).getValue() == null);
        return mUartService.getCharacteristics().get(1).getValue();
    }

    /**********************************************************************************************/
    // BleManagerListener interface implementation
    @Override
    public void onConnected() {
    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onServicesDiscovered() {

        if(null == mBleManager)
        {
            Log.d(TAG, "Cannot use mBleManager, not instance associated");
        }

        mUartService = mBleManager.getGattService(UUID_SERVICE);
        if(null == mUartService)
        {
            Log.d(TAG, "Cannot found service UART for this TAG");
        }

        enableRxNotifications();
    }

    @Override
    public void onDataAvailable(BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onDataAvailable(BluetoothGattDescriptor descriptor) {
    }

    @Override
    public void onReadRemoteRssi(int rssi) {

    }
}
