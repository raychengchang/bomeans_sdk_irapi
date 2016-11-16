package com.bomeans.testirapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bomeans.irapi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    // apply a API KEY from Bomeans to run this demo
    private String BOMEANS_SDK_API_KEY = "";

    private List<TypeInfo> mTypeList = new ArrayList<>();
    private Map<TypeInfo, List<BrandInfo>> mBrands = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSDK();
    }

    private void initializeSDK() {

        // initialize the SDK
        IRAPI.init(BOMEANS_SDK_API_KEY, getApplicationContext());

        // select server if needed
        IRAPI.switchToChineseServer(true);

        // get all supported types
        getAllTypes();
    }

    private void getAllTypes() {
        // get all supported types
        IRAPI.getTypeList("TW", true, new IGetTypeListCallback() {
            @Override
            public void onDataReceived(List<TypeInfo> typeList) {
                mTypeList = typeList;
                for (TypeInfo type : typeList) {
                    Log.d(DBG_TAG, String.format("Type: id=%s, name_en=%s, name=%s", type.typeId, type.typeNameEN, type.typeNameLocalized));
                }

                // get all supported brands
                for (TypeInfo typeInfo : mTypeList) {
                    getAllBrands(typeInfo);
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, "[ERROR] failed to get type list.");
            }
        });
    }

    private void getAllBrands(final TypeInfo typeInfo) {

        IRAPI.getBrandList("TW", typeInfo.typeId, true, true, new IGetBrandListCallback() {

            @Override
            public void onDataReceived(List<BrandInfo> brandList) {

                mBrands.put(typeInfo, brandList);

                for (BrandInfo brand : brandList) {
                    Log.d(DBG_TAG, String.format("Brand: id=%s, name_en=%s, name=%s", brand.brandId, brand.brandNameEN, brand.brandNameLocalized));
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("[ERROR] failed to get brand lists %s", typeInfo.typeNameLocalized));
            }
        });
    }

    private void getModelList() {

    }
}
