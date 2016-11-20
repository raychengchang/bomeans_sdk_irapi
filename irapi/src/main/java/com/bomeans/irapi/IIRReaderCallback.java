package com.bomeans.irapi;

/**
 * Created by ray on 2016/11/20.
 */

public interface IIRReaderCallback {

    void onReaderCreated(IIRReader irReader);

    void onReaderCreateFailed();
}
