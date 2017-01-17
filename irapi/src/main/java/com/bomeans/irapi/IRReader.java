package com.bomeans.irapi;

import com.bomeans.IRKit.BIRReader;
import com.bomeans.IRKit.ConstValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a wrapper class of BIRReader, for remove the IRKit dependency in code.
 * Nothing different from using IRKit.BIRReader directly actually.
 *
 */

public class IRReader implements IIRReader {

    private BIRReader mIrReader;

    public IRReader(BIRReader birReader) {
        mIrReader = birReader;
    }

    @Override
    public boolean load(byte[] data, boolean isPayloadOnly, boolean isCompressedFormat) {
        if (null != mIrReader) {
            return mIrReader.load(data, isPayloadOnly, isCompressedFormat);
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<ReaderMatchResult> getBestMatches() {

        ArrayList<ReaderMatchResult> readerResultList = new ArrayList<>();

        if (null != mIrReader) {
            ArrayList<BIRReader.ReaderMatchResult> resultList = mIrReader.getBestMatches();
            if (null != resultList) {
                for (int i = 0; i < resultList.size(); i++) {
                    readerResultList.add(new ReaderMatchResult(
                            resultList.get(i).formatId,
                            resultList.get(i).customCode,
                            resultList.get(i).keyCode
                    ));
                }
            }
        }
        return readerResultList;
    }

    @Override
    public ArrayList<ReaderMatchResult> getPossibleMatches() {
        ArrayList<ReaderMatchResult> readerResultList = new ArrayList<>();

        if (null != mIrReader) {
            ArrayList<BIRReader.ReaderMatchResult> resultList = mIrReader.getPossibleMatches();
            if (null != resultList) {
                for (int i = 0; i < resultList.size(); i++) {
                    readerResultList.add(new ReaderMatchResult(
                            resultList.get(i).formatId,
                            resultList.get(i).customCode,
                            resultList.get(i).keyCode
                    ));
                }
            }
        }
        return readerResultList;
    }

    @Override
    public ArrayList<ReaderMatchResult> getAllMatches() {

        ArrayList<ReaderMatchResult> readerResultList = new ArrayList<>();

        if (null != mIrReader) {
            ArrayList<BIRReader.ReaderMatchResult> resultList = mIrReader.getAllMatches();
            if (null != resultList) {
                for (int i = 0; i < resultList.size(); i++) {
                    readerResultList.add(new ReaderMatchResult(
                            resultList.get(i).formatId,
                            resultList.get(i).customCode,
                            resultList.get(i).keyCode
                    ));
                }
            }
        }

        return readerResultList;
    }

    @Override
    public int getWaveCount() {
        if (null != mIrReader) {
            return mIrReader.getWaveCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getFrequency() {
        if (null != mIrReader) {
            return mIrReader.getFrequency();
        } else {
            return 0;
        }
    }

    @Override
    public void startLearningAndGetData(PREFER_REMOTE_TYPE preferRemoteType, final IIRReaderFormatMatchCallback callback) {

        if (null != mIrReader) {

            BIRReader.PREFER_REMOTE_TYPE type;
            switch (preferRemoteType) {
                case AC:
                    type = BIRReader.PREFER_REMOTE_TYPE.AC;
                    break;
                case TV:
                    type = BIRReader.PREFER_REMOTE_TYPE.TV;
                    break;
                case Auto:
                default:
                    type = BIRReader.PREFER_REMOTE_TYPE.Auto;
                    break;
            }
            mIrReader.startLearningAndGetData(type, new BIRReader.BIRReaderFormatMatchCallback() {
                @Override
                public void onFormatMatchSucceeded(BIRReader.ReaderMatchResult readerMatchResult) {
                    if (null != callback) {
                        ReaderMatchResult myResult = new ReaderMatchResult(
                                readerMatchResult.formatId,
                                readerMatchResult.customCode,
                                readerMatchResult.keyCode
                        );
                        callback.onFormatMatchSucceeded(myResult);
                    }
                }

                @Override
                public void onFormatMatchFailed(BIRReader.FormatParsingErrorCode formatParsingErrorCode) {
                    if (null != callback) {
                        callback.onFormatMatchFailed(getFormatParsingErrorCode(formatParsingErrorCode));
                    }
                }

                @Override
                public void onLearningDataReceived(byte[] bytes) {
                    if (null != callback) {
                        callback.onLearningDataReceived(bytes);
                    }
                }

                @Override
                public void onLearningDataFailed(BIRReader.LearningErrorCode learningErrorCode) {
                    if (null != callback) {
                        callback.onLearningDataFailed(getLearningErrorCode(learningErrorCode));
                    }
                }
            });
        }
    }

    @Override
    public void startLearningAndSearchCloud(boolean isNewSearch, PREFER_REMOTE_TYPE preferRemoteType, final IIRReaderRemoteMatchCallback callback) {

        if (null != mIrReader) {

            BIRReader.PREFER_REMOTE_TYPE type;
            switch (preferRemoteType) {
                case AC:
                    type = BIRReader.PREFER_REMOTE_TYPE.AC;
                    break;
                case TV:
                    type = BIRReader.PREFER_REMOTE_TYPE.TV;
                    break;
                case Auto:
                default:
                    type = BIRReader.PREFER_REMOTE_TYPE.Auto;
                    break;
            }

            mIrReader.startLearningAndSearchCloud(isNewSearch, type, new BIRReader.BIRReaderRemoteMatchCallback() {
                @Override
                public void onRemoteMatchSucceeded(List<BIRReader.RemoteMatchResult> list) {
                    List<RemoteMatchResult> myResultList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        myResultList.add(new RemoteMatchResult(
                                list.get(i).typeID,
                                list.get(i).brandID,
                                list.get(i).modelID
                        ));
                    }

                    if (null != callback) {
                        callback.onRemoteMatchSucceeded(myResultList);
                    }
                }

                @Override
                public void onRemoteMatchFailed(BIRReader.CloudMatchErrorCode cloudMatchErrorCode) {
                    if (null != callback) {
                        callback.onRemoteMatchFailed(getCloudMatchErrorCode(cloudMatchErrorCode));
                    }
                }

                @Override
                public void onFormatMatchSucceeded(List<BIRReader.ReaderMatchResult> list) {

                    List<ReaderMatchResult> myResultList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        myResultList.add(new ReaderMatchResult(
                                list.get(i).formatId,
                                list.get(i).customCode,
                                list.get(i).keyCode
                        ));
                    }

                    if (null != callback) {
                        callback.onFormatMatchSucceeded(myResultList);
                    }
                }

                @Override
                public void onFormatMatchFailed(BIRReader.FormatParsingErrorCode formatParsingErrorCode) {
                    if (null != callback) {
                        callback.onFormatMatchFailed(getFormatParsingErrorCode(formatParsingErrorCode));
                    }
                }
            });
        }
    }

    @Override
    public void reset() {

        if (null != mIrReader) {
            mIrReader.reset();
        }
    }

    @Override
    public Boolean stopLearning() {
        if (null != mIrReader) {
            return (ConstValue.BIROK == mIrReader.stopLearning());
        } else {
            return false;
        }
    }

    @Override
    public Boolean sendLearningData(byte[] learningData) {
        if (null != mIrReader) {
            return (ConstValue.BIROK == mIrReader.sendLearningData(learningData));
        } else {
            return false;
        }
    }

    private CloudMatchErrorCode getCloudMatchErrorCode(BIRReader.CloudMatchErrorCode errCode) {
        switch (errCode) {
            case LearningModeFailed:
                return CloudMatchErrorCode.LearningModeFailed;
            case NoValidLearningData:
                return CloudMatchErrorCode.NoValidLearningData;
            case UnrecognizedFormat:
            default:
                return CloudMatchErrorCode.UnrecognizedFormat;
        }
    }

    private FormatParsingErrorCode getFormatParsingErrorCode(BIRReader.FormatParsingErrorCode errCode) {
        switch (errCode) {
            case LearningModeFailed:
                return FormatParsingErrorCode.LearningModeFailed;
            case NoValidLearningData:
                return FormatParsingErrorCode.NoValidLearningData;
            case UnrecognizedFormat:
            default:
                return FormatParsingErrorCode.UnrecognizedFormat;
        }
    }

    private LearningErrorCode getLearningErrorCode(BIRReader.LearningErrorCode errCode) {
        switch (errCode) {
            case LearningModeFailed:
                return LearningErrorCode.LearningModeFailed;
            case IncorrectLearnedData:
                return LearningErrorCode.IncorrectLearnedData;
            case TimeOut:
            default:
                return LearningErrorCode.TimeOut;
        }
    }
}
