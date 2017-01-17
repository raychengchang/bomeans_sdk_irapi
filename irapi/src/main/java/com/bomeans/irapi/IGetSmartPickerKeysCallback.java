package com.bomeans.irapi;

import java.util.List;

/**
 * Created by ray on 2016/12/31.
 */

public interface IGetSmartPickerKeysCallback {
    void onDataReceived(List<String> keyIdList);
    void onError(int errorCode);
}
