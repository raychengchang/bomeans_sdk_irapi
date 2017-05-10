package com.bomeans.irapi;

public interface ITVSmartPicker {

	/**
	 * Get the key id for testing
	 * @return
	 */
	String getPickerKey();
	/**
	 * Get the model name for testing
	 * for AC
	 * @return
	 */
	RemoteInfo getModel();

	/**
	 * set test model key Quantity
	 * for AC
	 * @return
	 */
	void setNum(int num);
	/**
	 * Transmit the IR signal of the current key
	 * @return
	 */
	int transmitIR();

	/**
	 * Pass the test result back to the picker
	 * @param isWorking true if the target appliance react to the IR signal, or false otherwise
	 */
	int setPickerResult(Boolean isWorking);

	/**
	 * Query the picker if the picker test has come to the end
	 * @return
	 */
	Boolean isPickerCompleted();

	/**
	 * Get the picker test result. The results are returned only when isPickerCompleted() returns true.
	 * @return picker results (could be multiple results), or null if the test is not yet completed.
	 */
	SmartPickerResult[] getPickerResult();

	/**
	 * To restart the smart picker
	 */
	void reset();

}
