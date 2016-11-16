package com.bomeans.irapi;

import com.bomeans.IRKit.TypeItem;

/**
 * Created by admin on 16/6/22.
 */
public class TypeInfo {
	public String typeId;
	public String typeNameEN;
	public String typeNameLocalized;

	public TypeInfo(String typeId, String typeNameEN, String typeNameLocalization) {
		this.typeId = typeId;
		this.typeNameEN = typeNameEN;
		this.typeNameLocalized = typeNameLocalization;
	}

	public TypeInfo(TypeItem typeItem) {
		this.typeId = typeItem.typeId;
		this.typeNameEN = typeItem.name;
		this.typeNameLocalized = typeItem.locationName;
	}
}
