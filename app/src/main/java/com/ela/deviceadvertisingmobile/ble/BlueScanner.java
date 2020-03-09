package com.ela.deviceadvertisingmobile.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.ela.deviceadvertisingmobile.ble.service.UartService;
import com.ela.deviceadvertisingmobile.view.DebugText;

public class BlueScanner implements BleManager.BleManagerListener{
    /** ---------- Arguments ---------- */
    private static BlueScanner instance = null;
    private static BluetoothLeScanner btScanner;
    private BleManager mBleManager;
    protected UartService uartService;
    private TextView textConnection;
    private boolean connected= false;

    /** ---------- Public functions -------- */
    public BlueScanner() {
    }

    /**
     *   get instance BlueScanner
     * @return Instance
     */
    public static BlueScanner getInstance() {
        if (instance == null)
            instance = new BlueScanner();
        return instance;
    }

    /**
     *   set the blue scanner to the instance
     * @param bt [BluetoothLeScanner] : Scanner Object
     */
    public void setBtScanner(Context context, BluetoothLeScanner bt)
    {
        btScanner = bt;
        // initialize blr manager
        mBleManager = BleManager.getInstance(context);
        uartService = new UartService(mBleManager);
        mBleManager.setBleListener(this);
    }

    /**
     *   Start a scan
     */
    public void startScanning() {
        System.out.println("start scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    /**
     *   Stop a scan
     */
    public void stopScanning() {
        System.out.println("stopping scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);

            }
        });
    }

    /**
     *   Connection to the tag selected
     * @param context
     * @param name [String] : Mac adress of the tag
     * @return sucess
     */
    public boolean connectToTag(Context context, String name, TextView txt)
    {
        if(txt != null)
            this.textConnection = txt;
        boolean isConnecting = mBleManager.connect(context, name);
        if (isConnecting)
        {
            return true;
        }
        return false;
    }

    /**
     *   Read the data on the Rx service
     * @return [String] : Hex value in a string
     */
    public String readRx()
    {
        if(uartService.readRxMessage() == null)
            return null;
        return bytesToHex(uartService.readRxMessage());
    }

    /**
     *   Disconnect the tag
     */
    public void disconnect() {
        mBleManager.disconnect();
    }

    /**
     *   Send a cmd to the tag
     * @param cmd
     * @return sucess
     */
    public boolean sendData(String cmd)
    {
        return uartService.sendData(cmd);
    }

    /** ---------- Private functions -------- */
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

    /**
     *   Scan result in a thread. Is called each tag read
     */
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //System.out.println(result.getDevice().getName());
            BleFactory.getInstance().setTag(result);
        }
    };

    /** ---------- Override functions -------- */
    @Override
    public void onConnected() {

    }

    @Override
    public void onConnecting() {
        if(this.textConnection != null) {
            DebugText.getInstance().write(">>> On Connecting...");
            this.textConnection.setText("In progress...");
        }
    }

    @Override
    public void onDisconnected() {
        this.connected = false;
        if(this.textConnection != null) {
            DebugText.getInstance().write(">>> On Disconnecting");
            this.textConnection.setText("Disconnected");
        }
    }

    @Override
    public void onServicesDiscovered() {
        DebugText.getInstance().write(">>> Service Discovered");
        DebugText.getInstance().write(">>> Connected");
        this.uartService.onServicesDiscovered();
        this.connected = true;
        if(this.textConnection != null)
            this.textConnection.setText("Connected");
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

