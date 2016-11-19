package com.bomeans.irapi;

import com.bomeans.IRKit.BIRIRBlaster;
import com.bomeans.IRKit.BIRReceiveDataCallback;
import com.bomeans.IRKit.ConstValue;

class IrBlasterWrapper implements BIRIRBlaster {

	private IIRBlaster mIrBlaster;
	private BIRReceiveDataCallback mReceiveDataCallback;
	
	public IrBlasterWrapper(IIRBlaster irBlaster) {
		mIrBlaster = irBlaster;
	}
	
	@Override
	public int sendData(byte[] irBlasterData) {
		if (null != mIrBlaster) {
			return mIrBlaster.transmitData(irBlasterData);
		}
		return ConstValue.BIRNoImplement;
	}

	@Override
	public int isConnection() {
		if (null != mIrBlaster) {
			if (mIrBlaster.isConnected()) {
				return ConstValue.BIROK;
			} else {
				return ConstValue.BIRNotFindWifiToIR;
			}
		}
		return ConstValue.BIRNoImplement;
	}

	@Override
	public void setReceiveDataCallback(BIRReceiveDataCallback birReceiveDataCallback) {

		mReceiveDataCallback = birReceiveDataCallback;
	}

}

