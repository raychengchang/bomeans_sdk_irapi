package com.bomeans.irapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2016/11/20.
 */

public interface IIRReader {


    /**
     * <b>Note</b> This method is for debug purpose.<br>
     * Not to call this method unless you understand its usage.<p>
     * Loading learning data into the reader for parsing.
     *
     * @param data data bytes to be parsed
     * @param isPayloadOnly true if the passing data bytes are payload data only (exclude the prefix, postfix, checksum,..etc),
     * or false if the passing data contains the full packet returned from the MCU (the full packet looks like this: 0xFF, 0x61, ..., 0xF0)
     * @param isCompressedFormat true if the containing payload data are in compressed format, or false if in plain(uncompressed) format.
     * (note: most likely it's in compressed format, so if unsure, set it to true)
     * @return true if the passing data is valid
     */
    boolean load(byte[] data, boolean isPayloadOnly, boolean isCompressedFormat);

    /**
     * Get the best matched format(s) of the passing learning data.
     * The best match(es) might not be only. But most likely you can pick the first entry as the best one.
     * Note the best match(es) might contain TV and AC results. You can filter the result by checking the
     * ReaderMatchResult::isAc().
     * @return best matched format(s) of the loaded learning data
     */
    ArrayList<ReaderMatchResult> getBestMatches();

    /**
     * All the other matched formats not treated as the "best" ones.
     * @return the possible matched format(s) of the loaded learning data
     */
    ArrayList<ReaderMatchResult> getPossibleMatches();

    /**
     * getBestMatches() + getPossibleMatches()
     * @return
     */
    ArrayList<ReaderMatchResult> getAllMatches();

    /**
     * <b>Note</b> This method is for debug purpose.<br>
     * Get the wave count (number of on/off IR signals) of the loaded learning data.
     *
     * @return the wave count (number of on/off IR signals) of the loaded data
     */
    int getWaveCount();

    /**
     * <b>Note</b> This method is for debug purpose.<br>
     * Get the carrier frequency of the loaded learning data.
     * @return carrier frequency, in Hz
     */
    int getFrequency();

    enum PREFER_REMOTE_TYPE {
        /**
         * The best matched format is decided by the parsing core.
         */
        Auto,
        /**
         * The best matched format is the AC format if any.<p>
         * If no AC format is matched, TV format will be selected
         */
        AC,
        /**
         * The best matched format is the TV format if any.<p>
         * If no TV format is matched, AC format will be selected
         */
        TV
    }

    /**
     * Start the learning process, and get the learned raw data as well as the matched format in the passing callback functions.<br>
     * <b>Note</b>: Only format matching is processed in this call. If you need to search the cloud server
     * for matching remote controllers, call <i>startLearningAndSearchCloud()</i>
     * @param preferRemoteType AC, TV, or Auto. This affects the decision of the "best match" returned.
     * @param callback To receive the learning/parsing results
     */
    void startLearningAndGetData(PREFER_REMOTE_TYPE preferRemoteType, IIRReaderFormatMatchCallback callback);

    /**
     * Start the learning process, get the matched format and matched remote controller(s) in the passing callback functions.<br>
     * @param isNewSearch Set to true if you are starting a new learning. Set to false if you are adding the learning to filter the previous matched result.
     * @param preferRemoteType AC, TV, or Auto. This affects the decision of the "best match" returned.
     * @param callback To receive the parsing/searching results
     */
    void startLearningAndSearchCloud(boolean isNewSearch, PREFER_REMOTE_TYPE preferRemoteType, IIRReaderRemoteMatchCallback callback);

    /**
     * Reset the internal learning states if you have previous called startLearningAndSearchCloud().
     */
    void reset();

    /**
     * Send the stop learning command to IR Blaster. (Return to normal mode)
     * @return true if succeeded, false otherwise
     */
    Boolean stopLearning();

    /**
     * Send the learned IR signal.<br>
     *
     * @param learningData The learning data get from startLearningAndGetData();
     */
    /**
     * Send the learned IR signal.<br>
     * @param learningData The learning data get from startLearningAndGetData()
     * @return true if succeeded, false otherwise
     */
    Boolean sendLearningData(byte[] learningData);

    /**
     * For keeping the format matching result of the learned data.
     *
     */
    class ReaderMatchResult {
        public String formatId;
        public long customCode = 0;
        public long keyCode = 0;

        public ReaderMatchResult(String formatId, long customCode, long keyCode) {
            this.formatId = formatId;
            this.customCode = customCode;
            this.keyCode = keyCode;
        }

        /**
         * Indicate if the matched format is AC format.
         * For non-AC format match (TV-like format), customCode/keyCode field will be filled if any.
         * @return true for AC format, false otherwise.
         */
        public Boolean isAc() {
            return ((this.customCode == -1) && (this.keyCode == -1));
        }
    }

    /**
     * for keeping the search result of the cloud server (for remote controller)
     *
     */
    class RemoteMatchResult {
        /**
         * type (category) id
         */
        public String typeId;
        /**
         * brand (make) id
         */
        public String brandId;
        /**
         * model id (also known as remote id)
         */
        public String remoteId;

        /**
         *
         * @param typeId type id
         * @param brandId brand id
         * @param remoteId remote id
         */
        public RemoteMatchResult(String typeId, String brandId, String remoteId) {
            this.typeId = typeId;
            this.brandId = brandId;
            this.remoteId = remoteId;
        }

    }

    interface IIRReaderRemoteMatchCallback {

        /**
         * The matched remote controller(s) of the loaded learning data.<br>
         * Note: the matched remote controller list is the result of accumulated learning data since the last call of reset().
         * @param remoteMatchResultList list of remote controller info
         */
        void onRemoteMatchSucceeded(List<RemoteMatchResult> remoteMatchResultList);

        /**
         * If failed to find the matched remote in the cloud database.
         * @param errorCode error code
         */
        void onRemoteMatchFailed(CloudMatchErrorCode errorCode);

        /**
         * Invoked if matched format of the learned result(s) is found.<br>
         * Note: The matched results are accumulated if not calling reset()
         * @param formatMatchResultList the matched result list
         */
        void onFormatMatchSucceeded(List<ReaderMatchResult> formatMatchResultList);

        /**
         * Invoked if no matched format of the learned result(s) is found.
         * @param errorCode error code
         */
        void onFormatMatchFailed(FormatParsingErrorCode errorCode);
    }

    enum LearningErrorCode {
        LearningModeFailed,
        TimeOut,
        IncorrectLearnedData
    }

    enum FormatParsingErrorCode {
        LearningModeFailed,
        NoValidLearningData,
        UnrecognizedFormat
    }

    enum CloudMatchErrorCode {
        LearningModeFailed,
        NoValidLearningData,
        UnrecognizedFormat,
        NoValidMatch,
        ServerError
    }

    interface IIRReaderFormatMatchCallback {

        /**
         * Invoked if matched format of learned result is found.<br>
         * @param formatMatchResult matched result
         */
        void onFormatMatchSucceeded(ReaderMatchResult formatMatchResult);

        /**
         * Invoked if there is no match for the learned result.
         * @param errorCode error code
         */
        void onFormatMatchFailed(FormatParsingErrorCode errorCode);

        /**
         * Invoked when the learning data is received.
         * @param learningData the learned IR signal data. This data can be stored and re-transmit by calling sendLearningData(byte[] data)
         */
        void onLearningDataReceived(byte[] learningData);

        /**
         * Invoked when the learning data is not received or is incorrect.
         * @param errorCode
         */
        void onLearningDataFailed(LearningErrorCode errorCode);
    }
}
