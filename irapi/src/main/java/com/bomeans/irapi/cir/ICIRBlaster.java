package com.bomeans.irapi.cir;

import java.util.ArrayList;

/**
 * Created by ray on 2016/11/25.
 */

public interface ICIRBlaster {

    int sendIR(int frequency, int[] patterns);

    int sendMultipleIR(int[] frequencyArray, ArrayList<int[]> patternsArray);

    int sendGeneralCommand(byte[] commandData);

    Boolean isConnected();

    void setReceiveDataCallback(IDataReceiveCallback2 callback);
}
