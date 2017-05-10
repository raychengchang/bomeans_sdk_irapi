package com.bomeans.irapi;

/**
 * Created by admin on 16/6/22.
 */
public interface ICreateSmartPickerCallback {
	void onPickerCreated(ITVSmartPicker smartPicker);
	void onError(int errorCode);
}
