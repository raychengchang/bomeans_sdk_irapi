package com.bomeans.irapi;

import java.util.List;

/**
 * Created by admin on 16/6/22.
 */
public interface IGetTypeListCallback {
	void onDataReceived(List<TypeInfo> typeList);
	void onError(int errorCode);
}
