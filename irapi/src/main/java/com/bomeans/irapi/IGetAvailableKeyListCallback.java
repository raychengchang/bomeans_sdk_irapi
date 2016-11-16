package com.bomeans.irapi;

import java.util.List;

/**
 * Created by admin on 16/6/23.
 */
public interface IGetAvailableKeyListCallback {
	void onDataReceived(List<KeyInfo2> keyList);
	void onError(int errorCode);
}
