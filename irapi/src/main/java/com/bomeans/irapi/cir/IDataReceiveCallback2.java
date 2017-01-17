package com.bomeans.irapi.cir;

/**
 * Created by ray on 2016/11/25.
 */

public interface IDataReceiveCallback2 {

    /**
     * if the learning data is received from hardware, invoke this function for further handling.
     * @param freq carrier frequency of the learned data
     * @param patterns waveform patterns of the learned data
     */
    void onLearningDataReceived(int freq, int[] patterns);

    /**
     * if leaning is failed (time-out or signal incorrect, depending on the response data from the hardware), invoke this method for further handling.
     */
    void onLearningDataFailed();

    /**
     * all general command data (except for the learning-data/learning-fail) should be passed back to
     * the SDK for further handling by invoking this method.
     * @param data command data bytes
     */
    void onGeneralCommandDataReceived(byte[] data);
}
