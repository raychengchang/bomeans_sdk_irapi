package com.bomeans.irapi;

import java.util.List;

/**
 * Created by admin on 16/6/21.
 */
public interface IGetRemoteListCallback {
	void onDataReceived(List<RemoteInfo> remoteList);
	void onError(int errorCode);
}
