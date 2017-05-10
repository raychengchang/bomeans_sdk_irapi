package com.bomeans.irapi;

import com.bomeans.IRKit.ACStoreDataItem;
import com.bomeans.IRKit.BIRGUIFeature;
import com.bomeans.IRKit.BIRKeyOption;
import com.bomeans.IRKit.BIRRemote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class IRRemote implements IIRRemote {

	private BIRRemote mIrRemote;
	
	public IRRemote(BIRRemote irRemote) {
		mIrRemote = irRemote;
	}
	
	@Override
	public String getBrandId() {
		if (null != mIrRemote) {
			return mIrRemote.getBrandName();
		}
		return null;
	}

	@Override
	public String getRemoteId() {
		if (null != mIrRemote) {
			return mIrRemote.getModuleName();
		}
		return null;
	}

	@Override
	public String[] getModels() {

		if (null != mIrRemote) {
            return mIrRemote.getMachineModels();
		}

        return new String[] {};
	}

	@Override
	public String[] getKeyList() {
		if (null != mIrRemote) {
			return mIrRemote.getAllKeys();
		} else {
			return null;
		}
	}

	@Override
	public Boolean startTransmitRepeatedIR(String keyId) {
		if (null != mIrRemote) {
			int errCode = mIrRemote.beginTransmitIR(keyId);
			return errCode == 0;
		} else {
			return false;
		}
	}

	@Override
	public void endTransmitRepeatedIR() {
		if (null != mIrRemote) {
			mIrRemote.endTransmitIR();
		}		
	}

	@Override
	public Boolean transmitIR(String keyId) {
		if (null != mIrRemote) {
			int errCode = mIrRemote.transmitIR(keyId, null);
            return errCode == 0;
		} else {
			return false;
		}
	}

	@Override
	public void setRepeatCount(int count) {
		if (null != mIrRemote) {
			mIrRemote.setRepeatCount(count);
		}
	}

	@Override
	public int getRepeatCount() {
		if (null != mIrRemote) {
			return mIrRemote.getRepeatCount();
		}
		return 1;
	}

	@Override
	public Boolean transmitIR(String keyId, String optionId) {
		if (null != mIrRemote) {
			int errCode = mIrRemote.transmitIR(keyId, optionId);
			return errCode == 0;
		} else {
			return false;
		}
	}

	@Override
	public String[] acGetActiveKeys() {
		
		if (null != mIrRemote) {
			return mIrRemote.getActiveKeys();
		} else {
			return null;
		}
	}

	@Override
	public ACKeyOptions acGetKeyOption(String keyId) {
		if (null != mIrRemote) {
			BIRKeyOption keyOption = mIrRemote.getKeyOption(keyId);
			return new ACKeyOptions(keyOption);
		} else {
			return null;
		}
	}

	@Override
	public Boolean acSetKeyOption(String keyId, String optionId) {
		if (null != mIrRemote) {
			int errCode = mIrRemote.setKeyOption(keyId, optionId);
			return errCode == 0;
		} else {
			return false;
		}
	}

	@Override
	public ACGUIFeatures acGetGuiFeatures() {
		if (null != mIrRemote) {
			BIRGUIFeature guiFeatures = mIrRemote.getGuiFeature();
            if (null == guiFeatures) {
                return null;
            } else {
                return new ACGUIFeatures(guiFeatures);
            }
		} else {
			return null;
		}
	}

	@Override
	public String[] acGetTimerKeys() {
		if (null != mIrRemote) {
			return mIrRemote.getTimerKeys();
		} else {
			return null;
		}
	}

	@Override
	public void acSetOffTime(int hour, int minute, int sec) {
		if (null != mIrRemote) {
			mIrRemote.setOffTime(hour, minute, sec);
		}		
	}

	@Override
	public void acSetOnTime(int hour, int minute, int sec) {
		if (null != mIrRemote) {
			mIrRemote.setOnTime(hour, minute, sec);
		}	
	}

	@Override
	public byte[] acGetStateData() {
		if (null != mIrRemote) {
			ACStoreDataItem[] dataItemArray = mIrRemote.getACStoreDatas();
			ACStateData[] stateDataArray = new ACStateData[dataItemArray.length];
			for (int i = 0; i < dataItemArray.length; i++) {
				stateDataArray[i] = new ACStateData(dataItemArray[i]);
			}
			
			return convertAcStateDataToByteArray(stateDataArray);
		} else {
			return null;
		}
	}

	@Override
	public Boolean acSetStateData(byte[] byteArray) {
		if (null != mIrRemote) {

			ACStateData[] stateDataArray = null;

			try {
				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
				stateDataArray = (ACStateData[]) in.readObject();
				in.close();

                ACStoreDataItem[] acStoreDataItemArray = new ACStoreDataItem[stateDataArray.length];
                for (int i = 0; i < stateDataArray.length; i++) {
                    acStoreDataItemArray[i] = stateDataArray[i].convertToACStoreDataItem();
                }
                mIrRemote.restoreACStoreDatas(acStoreDataItemArray);

			} catch(ClassNotFoundException e) {
				e.printStackTrace();

			} catch(IOException e) {
				e.printStackTrace();

			}

			if (null != stateDataArray) {

				ACStoreDataItem[] dataItemArray = new ACStoreDataItem[stateDataArray.length];
				for (int i = 0; i < stateDataArray.length; i++) {
					dataItemArray[i] = stateDataArray[i].convertToACStoreDataItem();
				}

				return mIrRemote.restoreACStoreDatas(dataItemArray);
			}
		}

		return false;
	}

	public byte[] convertAcStateDataToByteArray(ACStateData[] dataArray) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(dataArray);

			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();

			return buf;

		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
