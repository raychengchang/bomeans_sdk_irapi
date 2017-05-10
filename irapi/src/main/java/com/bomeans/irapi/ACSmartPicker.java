package com.bomeans.irapi;


import com.bomeans.IRKit.ACSmartInfo;
import com.bomeans.IRKit.ConstValue;
import com.bomeans.IRKit.RemoteUID;

public class ACSmartPicker implements ITVSmartPicker{


    //    private BIRACPicker biracPicker;
    private com.bomeans.IRKit.ACSmartPicker biracPicker;
    private String mCurrentKey = null;
    RemoteUID mResults = null;
    private Boolean mCompleted = false;
    RemoteInfo model;
    int result;

    public ACSmartPicker(com.bomeans.IRKit.ACSmartPicker acSmartPicker){
        this.biracPicker = acSmartPicker;
    }

    public void setNum(int num){
        biracPicker.setTryKeyNum(num);
    }

    public RemoteInfo getModel(){
        return model;
    }
    public String getPickerKey() {
        if(null == biracPicker){
            return null;
        }

        if(null == mCurrentKey){
            ACSmartInfo picker = biracPicker.begin();
            mCurrentKey = picker.key;
            model = new RemoteInfo(picker.remote.model,
                    picker.remote.machineModel,
                    picker.remote.country,
                    picker.remote.releaseTime);
        }
        return mCurrentKey;
    }

    public int transmitIR() {
        if(null != biracPicker){
            return biracPicker.transmitIR();
        }
        return ConstValue.BIRTransmitFail;
    }


    public SmartPickerResult[] getPickerResult() {
        SmartPickerResult[] smartPickerResult;
        if(mCompleted) {
            mResults = new RemoteUID(biracPicker.getPickerResult().typeID,
                    biracPicker.getPickerResult().brandID,
                    biracPicker.getPickerResult().modelID);
            smartPickerResult = new SmartPickerResult[]{new SmartPickerResult(mResults)};

        }else {
            smartPickerResult = null;
        }
        return smartPickerResult;
    }

    @Override
    public Boolean isPickerCompleted() {
        return mCompleted;
    }

    @Override
    public int setPickerResult(Boolean isWorking) {
        result = biracPicker.keyResult(isWorking);
        switch (result){
            case ConstValue.BIR_PFind:
                mCompleted = true;
                break;
            case ConstValue.BIR_PNext:
                mCurrentKey=  biracPicker.getNextKey();
                model=new RemoteInfo(biracPicker.getNextModel().model,
                        biracPicker.getNextModel().machineModel,
                        biracPicker.getNextModel().country,
                        biracPicker.getNextModel().releaseTime);
                break;
            case ConstValue.BIR_PFail:
                mCompleted=true;
                model = null;
                break;
        }

        return result;
    }
    @Override
    public void reset() {

    }
}
