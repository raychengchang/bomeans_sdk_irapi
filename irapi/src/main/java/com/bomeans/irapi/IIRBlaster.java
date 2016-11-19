package com.bomeans.irapi;


public interface IIRBlaster {

	Boolean isConnected();

	int transmitData(byte[] data);

	void setReceiveDataCallback(IDataReceiveCallback callback);
}
