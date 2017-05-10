package com.bomeans.irapi;

import android.util.Log;

import com.bomeans.IRKit.BIRTVPicker;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.RemoteUID;

public class TVSmartPicker implements ITVSmartPicker {
	private static final String TAG = "sss";
	private BIRTVPicker mTvPicker;
	
	private String mCurrentKey = null;
	private Boolean mCompleted = false;
	private SmartPickerResult[] mResults = null;
	
	public TVSmartPicker(BIRTVPicker picker) {
		mTvPicker = picker;
		mCurrentKey = null;
		mCompleted = false;
	}

	@Override
	public String getPickerKey() {
		
		if (null == mTvPicker) {
			Log.e(TAG, "getPickerKey:mtvpicker" );
			return null;
		}
		
		if (mCompleted) {
			Log.e(TAG, "getPickerKey: mcompleted" );
			return null;
		}
		
		if (null == mCurrentKey) {
			Log.e(TAG, "getPickerKey: begin" );
			mCurrentKey = mTvPicker.begin();
		} else {
			Log.e(TAG, "getPickerKey: getnextkey" );
			mCurrentKey = mTvPicker.getNextKey();
		}
		
		return mCurrentKey;
	}

	@Override
	public int transmitIR() {
		if (null != mTvPicker) {
			return mTvPicker.transmitIR();
		}
		return ConstValue.BIRTransmitFail;
	}

	@Override
	public int setPickerResult(Boolean isWorking) {
		
		int result = mTvPicker.keyResult(isWorking);
		RemoteUID[] remoteUidArray;
		
	 	 switch (result) {
		case ConstValue.BIR_PFind:
			mCompleted = true;
			remoteUidArray = mTvPicker.getPickerResult();
			break;

		case ConstValue.BIR_PFail:
			mCompleted = true;
			remoteUidArray = new RemoteUID[0];
			break;

		case ConstValue.BIR_PNext:
			mCompleted = false;
			remoteUidArray = null;
			break;

		case ConstValue.BIR_PUnknow:
		default:
			mCompleted = false;
			remoteUidArray = null;
			break;
		}
	
		if (mCompleted && remoteUidArray != null) {
			mResults = new SmartPickerResult[remoteUidArray.length];
			for (int i = 0; i < remoteUidArray.length; i++) {
				mResults[i] = new SmartPickerResult(remoteUidArray[i]);
			}
		}
		return result;
	}

	@Override
	public Boolean isPickerCompleted() {
		return mCompleted;
	}

	@Override
	public SmartPickerResult[] getPickerResult() {
		if (mCompleted) {
			return mResults;
		} else {	
			return null;
		}
	}

    @Override
    public void reset() {
        mCurrentKey = null;
        mCompleted = false;
        mResults = null;
        if (null != mTvPicker) {
            mTvPicker.begin();
        }
    }

	@Override
	public void setNum(int num) {

	}

	@Override
	public RemoteInfo getModel() {
		return null;
	}
}
