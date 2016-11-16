package com.bomeans.irapi;

import com.bomeans.IRKit.BrandItem;

/**
 * Created by admin on 16/6/21.
 */
public class BrandInfo {
	public String brandId;
	public String brandNameEN;
	public String brandNameLocalized;

	public BrandInfo(String brandId, String brandNameEN, String brandNameLocalization) {
		this.brandId = brandId;
		this.brandNameEN = brandNameEN;
		this.brandNameLocalized = brandNameLocalization;
	}

	public BrandInfo(BrandItem brandItem) {
		this.brandId = brandItem.brandId;
		this.brandNameEN = brandItem.name;
		this.brandNameLocalized = brandItem.locationName;
	}

}
