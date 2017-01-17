package com.bomeans.irapi.cir;

import com.bomeans.IRKit.BIRIrHW;
import com.bomeans.IRKit.BIRReceiveDataCallback2;
import com.bomeans.IRKit.ConstValue;

import java.util.ArrayList;

/**
 * Created by ray on 2016/11/25.
 */

public class CIRBlasterWrapper implements BIRIrHW {

    private ICIRBlaster mIrBlaster;
    private BIRReceiveDataCallback2 mReceiveDataCallback;

    public CIRBlasterWrapper(ICIRBlaster irBlaster) {
        mIrBlaster = irBlaster;
    }

    @Override
    public int SendIR(int frequency, int[] patterns) {

        if (null != mIrBlaster) {
            return mIrBlaster.sendIR(frequency, patterns);
        }

        return ConstValue.BIRNoImplement;
    }

    @Override
    public int sendMultipIR(int[] frequencyArray, ArrayList<int[]> patternsArray) {
        if (null != mIrBlaster) {
            return mIrBlaster.sendMultipleIR(frequencyArray, patternsArray);
        }
        return ConstValue.BIRNoImplement;
    }

    @Override
    public int sendUARTCommand(byte[] commandData) {
        if (null != mIrBlaster) {
            return mIrBlaster.sendGeneralCommand(commandData);
        }
        return ConstValue.BIRNoImplement;
    }

    @Override
    public int getHwType() {
        return 0;
    }

    @Override
    public int isConnection() {

        if (null != mIrBlaster) {
            return mIrBlaster.isConnected() ? ConstValue.BIROK : ConstValue.BIRNotFindWifiToIR;
        }
        return ConstValue.BIRNoImplement;
    }

    @Override
    public void setReceiveDataCallback(BIRReceiveDataCallback2 birReceiveDataCallback2) {

        mReceiveDataCallback = birReceiveDataCallback2;

        if (null != mIrBlaster) {
            mIrBlaster.setReceiveDataCallback(new IDataReceiveCallback2() {

                @Override
                public void onLearningDataReceived(int freq, int[] patterns) {

                    if (null != mReceiveDataCallback) {
                        mReceiveDataCallback.onLearningDataReceived(freq, patterns);
                    }
                }

                @Override
                public void onLearningDataFailed() {
                    if (null != mReceiveDataCallback) {
                        mReceiveDataCallback.onLearningFailed();
                    }
                }

                @Override
                public void onGeneralCommandDataReceived(byte[] data) {
                    if (null != mReceiveDataCallback) {
                        mReceiveDataCallback.onDataReceived(data);
                    }
                }
            });
        }
    }
}
