package com.bomeans.testirapi;

import android.util.Log;

import com.bomeans.irapi.cir.ICIRBlaster;
import com.bomeans.irapi.cir.IDataReceiveCallback2;

import java.util.ArrayList;

/**
 * Created by ray on 2016/11/25.
 */

public class MyCirBlaster implements ICIRBlaster {

    private String DBG_TAG = "IRAPI_MyIrBlaster";

    IDataReceiveCallback2 mDataReceiveCallback;

    @Override
    public int sendIR(int frequency, int[] patterns) {
        String info = String.format("Transmit IR: %dHz,", frequency);
        for (int i = 0; i < patterns.length; i++) {
            info += String.format("%d,", patterns[i]);
        }
        Log.d(DBG_TAG, info);

        return 0;   // no error
    }

    @Override
    public int sendMultipleIR(int[] frequencyArray, ArrayList<int[]> patternsArray) {
        for (int irIdx = 0; irIdx < frequencyArray.length; irIdx++) {
            String info = String.format("Transmit IR: %dHz,", frequencyArray[irIdx]);
            for (int i = 0; i < patternsArray.get(irIdx).length; i++) {
                info += String.format("%d,", patternsArray.get(irIdx)[i]);
            }
            Log.d(DBG_TAG, info);
        }

        return 0;   // no error
    }

    @Override
    public int sendGeneralCommand(byte[] commandData) {
        String info = String.format("Transmit %d bytes:", commandData.length);
        for (int i = 0; i < commandData.length; i++) {
            info += String.format("%02X,", commandData[i]);
        }
        Log.d(DBG_TAG, info);

        return 0;   // no error
    }

    @Override
    public Boolean isConnected() {
        // just return true if the underlying hardware is connected.
        return true;
    }

    @Override
    public void setReceiveDataCallback(IDataReceiveCallback2 callback) {
        mDataReceiveCallback = callback;
    }

    public void onGeneralCommandDataReceived(byte[] receivedData) {

        // you might need to check the data integrity.

        if (null != mDataReceiveCallback) {
            mDataReceiveCallback.onGeneralCommandDataReceived(receivedData);

            String info = String.format("Received %d bytes:", receivedData.length);
            for (int i = 0; i < receivedData.length; i++) {
                info += String.format("%02X,", receivedData[i]);
            }
            Log.d(DBG_TAG, info);
        }
    }

    public void onLearningDataReceived(int frequency, int[] patterns) {
        if (null != mDataReceiveCallback) {
            mDataReceiveCallback.onLearningDataReceived(frequency, patterns);

            String info = String.format("Received Learning Data: %dHz,", frequency);
            for (int i = 0; i < patterns.length; i++) {
                info += String.format("%d,", patterns[i]);
            }
            Log.d(DBG_TAG, info);
        }
    }
}
