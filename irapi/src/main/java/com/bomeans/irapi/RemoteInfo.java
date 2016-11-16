package com.bomeans.irapi;

/**
 * Created by admin on 16/6/21.
 */
public class RemoteInfo {
	public String remoteId;
	public String supportedModels;
	public String locationCode;
	public String registerDate;

	public RemoteInfo(String remoteId, String supportedModels, String locationCode, String registerDate) {
		this.remoteId = remoteId;
		this.supportedModels = supportedModels;
		this.locationCode = locationCode;
		this.registerDate = registerDate;
	}
}
