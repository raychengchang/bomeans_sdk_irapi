package com.bomeans.testirapi;

import android.util.Log;

import com.bomeans.IRKit.ConstValue;
import com.bomeans.irapi.IDataReceiveCallback;
import com.bomeans.irapi.IIRBlaster;

/**
 * Created by ray on 2016/11/19.
 */

public class MyIrBlaster implements IIRBlaster {

    private String DBG_TAG = "IRAPI_MyIrBlaster";

    IDataReceiveCallback mDataReceiveCallback;

    @Override
    public Boolean isConnected() {
        // just return true if the underlying hardware is connected.
        return true;
    }

    @Override
    public int transmitData(byte[] data) {

        // here is where you get the IR data.
        // This data bytes need to be relayed to the Bomeans MCU vai
        // physical connection (such as UART or I2C) or wireless connection
        // (such as WiFi, BLE, or Zigbee passthrough.
        String info = String.format("Transmit %d bytes:", data.length);
        for (int i = 0; i < data.length; i++) {
            info += String.format("%02X,", data[i]);
        }
        Log.d(DBG_TAG, info);

        return ConstValue.BIROK;
    }

    @Override
    public void setReceiveDataCallback(IDataReceiveCallback callback) {
        // You need to keep this callback function and call the
        // callback.onDataReceive(byte[] receivedData) with the
        // received data bytes as the parameter.
        mDataReceiveCallback = callback;
    }

    public void onDataArrived(byte[] receivedData) {

        // you might need to check the data integrity.

        if (null != mDataReceiveCallback) {
            mDataReceiveCallback.onDataReceived(receivedData);

            String info = String.format("Received %d bytes:", receivedData.length);
            for (int i = 0; i < receivedData.length; i++) {
                info += String.format("%02X,", receivedData[i]);
            }
            Log.d(DBG_TAG, info);
        }
    }
}
