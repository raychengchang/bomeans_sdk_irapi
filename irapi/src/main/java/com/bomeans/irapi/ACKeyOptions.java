package com.bomeans.irapi;

import com.bomeans.IRKit.BIRKeyOption;

public class ACKeyOptions {

	public int currentOption;
	public String[] options;
	public boolean enable;
	
	public ACKeyOptions(BIRKeyOption keyOption) {
		
		this.currentOption = keyOption.currentOption;
		this.options = keyOption.options;
		this.enable = keyOption.enable;
	}
}
