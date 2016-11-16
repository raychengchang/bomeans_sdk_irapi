package com.bomeans.irapi;

import com.bomeans.IRKit.RemoteUID;

public class SmartPickerResult {
	
	public String typeId;
	public String brandId;
	public String remoteId;
	
	public SmartPickerResult() {
		this.typeId = "";
		this.brandId = "";
		this.remoteId = "";
	}
	
	public SmartPickerResult(RemoteUID remoteUid) {
		this.typeId = remoteUid.typeID;
		this.brandId = remoteUid.brandID;
		this.remoteId = remoteUid.modelID;
	}
}
