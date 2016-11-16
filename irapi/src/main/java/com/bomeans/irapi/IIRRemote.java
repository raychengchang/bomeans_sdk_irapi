package com.bomeans.irapi;



public interface IIRRemote {
	
	/**
	 * Get the brand Id.
	 * @return
	 */
	String getBrandId();

	/**
	 * Get the ID of this remote. Remote ID is used to exclusively distinguish the remote controllers.
	 * @return
	 */
	String getRemoteId();

	/**
	 * Get all the key IDs contains in this remote
	 * @return
	 */
	String[] getKeyList();

	/**
	 * (<b>TV-like remote only</b>) start transmitting repeated IR signals
	 * @param keyId
	 * @return
	 */
	Boolean startTransmitRepeatedIR(String keyId);
	/**
	 * (<b>TV-like remote only</b>) end transmitting repeated IR signals
	 */
	void endTransmitRepeatedIR();

	/**
	 * For TV-like remote: transmit single shot IR signals (with the repeat count set by setRepeatCount());<br>
	 * For AC remote: transmit IR signals without specify the option. (So the option will
	 * be selected according to the remote default behavior)
	 * @param keyId
	 * @return
	 */
	Boolean transmitIR(String keyId);

	/**
	 * Set the repeat count of the IR signals while invoking transmitIR()
	 */
	void setRepeatCount(int count);
	int getRepeatCount();

	/**
	 * (AC remote only) transmit IR signals with the specified key and the key option.
	 * @param keyId
	 * @param optionId
	 * @return
	 */
	Boolean transmitIR(String keyId, String optionId);

	//-------------------------------
	// for AC remote only
	//-------------------------------
	/**
	 * (<b>AC remote only</b>) The AC remote should call getAllKeys() to get the full list of keys supported
	 * by the remote. And call getActiveKeys() to get the list of currently available keys.
	 * The currently available keys here indicate the keys that can be manipulated in the current
	 * state of the AC remote. (Available keys could be different according to the current operation
	 * mode of the AC remote.)
	 * @return
	 */
	String[] acGetActiveKeys();

	/**
	 * (<b>AC remote only</b>) Get the key options of the specified key.
	 * @param keyId
	 * @return
	 */
	ACKeyOptions acGetKeyOption(String keyId);


	/**
	 * (<b>AC remote only</b>) Get the GUI features of the remote.
	 * GUI features are here for reference in GUI design only.
	 * @return
	 */
	ACGUIFeatures acGetGuiFeatures();

	/*
	 * Get available timer key IDs.
	 */
	String[] acGetTimerKeys();

	// 設定冷氣的 off time
	/**
	 * (<b>AC remote only</b>) Set the OFF timer time by giving the explicit OFF time.
	 * @param hour 0-23
	 * @param minute 0 - 59
	 * @param sec 0 - 59
	 */
	void acSetOffTime(int hour, int minute, int sec);

	/**
	 * (<b>AC remote only</b>) Set the ON timer time by giving the explicit ON time.
	 * @param hour 0-23
	 * @param minute 0 - 59
	 * @param sec 0 - 59
	 */
	void acSetOnTime(int hour, int minute, int sec);

	/**
	 * (<b>AC remote only</b>) Get the current state data of the AC remote. State data represents the
	 * current state of the AC remote. The returned state data should be handled and saved by
	 * the App developer in case the AC remote state is to be restored.<br><br>
	 * Note: for a newly created AC remote, the internal state will be reset to default.
	 * To restore the previous state of the remote, you need to call acGetStateData() to get
	 * the state data and save it somewhere, and invoke acSetStateData() to the AC remote only
	 * it is re-created.
	 *
	 * @return
	 */
	byte[] acGetStateData();

	/**
	 * (<b>AC remote only</b>)
	 * @param stateDataArray
	 * @return
	 */
	Boolean acSetStateData(byte[] stateDataArray);


}


