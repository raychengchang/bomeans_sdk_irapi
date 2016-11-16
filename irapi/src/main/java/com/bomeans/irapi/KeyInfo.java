package com.bomeans.irapi;

import com.bomeans.IRKit.KeyItem;

/**
 * Created by admin on 16/6/29.
 */
public class KeyInfo {
	public String keyId;
	public String keyNameEN;
	public String keyNameLocalized;

	public KeyInfo(String keyId, String keyNameEN, String keyNameLocalized) {
		this.keyId = keyId;
		this.keyNameEN = keyNameEN;
		this.keyNameLocalized = keyNameLocalized;
	}

	/**
	 * Map the IRKit.KeyItem into KeyInfo
	 * @param keyItem
	 */
	public KeyInfo(KeyItem keyItem) {
		this.keyId = keyItem.keyId;
		this.keyNameEN = keyItem.name;
		this.keyNameLocalized = keyItem.locationName;
	}
}
