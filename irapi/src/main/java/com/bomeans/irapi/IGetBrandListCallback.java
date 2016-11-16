package com.bomeans.irapi;

import java.util.List;

/**
* Created by admin on 16/6/21.
*/
public interface IGetBrandListCallback {
   void onDataReceived(List<BrandInfo> brandList);
   void onError(int errorCode);
}
