package com.bomeans.irapi;

import com.bomeans.IRKit.KeyName;

/**
 * Created by admin on 16/6/23.
 */
public class KeyInfo2 {
	public String typeId;
	public String keyId;
	public String keyNameLocalized;

	public KeyInfo2(String typeId, String keyId, String keyNameLocalization) {
		this.typeId = typeId;
		this.keyId = keyId;
		this.keyNameLocalized = keyNameLocalization;
	}

	/**
	 * Map the IRKit.KeyName to KeyInfo2
	 * @param keyName
	 */
	public KeyInfo2(KeyName keyName) {
		this.keyId = keyName.keyId;
		this.keyNameLocalized = keyName.name;
		this.typeId = keyName.type;
	}
}
