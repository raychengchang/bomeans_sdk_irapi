package com.bomeans.irapi;

/**
 * Created by admin on 16/6/21.
 */
public interface ICreateRemoteCallback {
	void onRemoteCreated(IRRemote remote);
	void onError(int errorCode);
}
