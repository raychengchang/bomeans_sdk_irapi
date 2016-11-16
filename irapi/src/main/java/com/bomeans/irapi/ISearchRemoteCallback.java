package com.bomeans.irapi;

/**
 * Created by admin on 16/6/29.
 */
public interface ISearchRemoteCallback {
	void onResultReceived(SearchResult result);
	void onError(int errorCode);
}
