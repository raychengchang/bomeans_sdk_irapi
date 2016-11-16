package com.bomeans.irapi;

import com.bomeans.IRKit.ACStoreDataItem;

import java.io.Serializable;

public class ACStateData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1630340786508420994L;
	
	public int mode;
	public String state;
	public int value;
	
	public ACStateData(ACStoreDataItem acDataItem)
	{
		this.mode = acDataItem.mode;
		this.state = acDataItem.state;
		this.value = acDataItem.value;
	}
	
	public ACStoreDataItem convertToACStoreDataItem() {
		ACStoreDataItem dataItem = new ACStoreDataItem();
		
		dataItem.mode = this.mode;
		dataItem.state = this.state;
		dataItem.value = this.value;
		
		return dataItem;
	}

}
