package com.bomeans.testirapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        // initialize SDK
        initializeSDK();

        // button: basic info, including supported types, brands, etc
        Button btnScanBasicInfo = (Button) findViewById(R.id.button_basic_info);
        btnScanBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get all supported types
                getAllTypes();
            }
        });

        // button: create tv-like remote controller
        Button btnCreateTVLikeRemote = (Button) findViewById(R.id.button_create_tv_remote);
        btnCreateTVLikeRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CreateTVRemoteActivity.class);
                intent.putExtra("type_id", "1");
                intent.putExtra("brand_id", "12");
                intent.putExtra("remote_id", "PANASONIC_N2QAYB_000846");
                MainActivity.this.startActivity(intent);

            }
        });

        // button: create tv-like universal remote controller
        Button btnCreateTVLikeUniversalRemote = (Button) findViewById(R.id.button_create_tv_univ_remote);
        btnCreateTVLikeUniversalRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTVUniversalRemoteActivity.class);
                intent.putExtra("type_id", "1");
                intent.putExtra("brand_id", "12");
                MainActivity.this.startActivity(intent);
            }
        });

        // button: create tv smart picker
        Button btnRunTVSmartPicker = (Button) findViewById(R.id.button_run_tv_smart_picker);
        btnRunTVSmartPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTVSmartPickerActivity.class);
                intent.putExtra("type_id", "1");
                intent.putExtra("brand_id", "3118");
                MainActivity.this.startActivity(intent);
            }
        });

        // button: create ac remote controller
        Button btnCreateACRemote = (Button) findViewById(R.id.button_create_ac_remote);
        btnCreateACRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateACRemoteActivity.class);
                intent.putExtra("type_id", "2");
                intent.putExtra("brand_id", "1449");
                intent.putExtra("remote_id", "DAIKIN-423A13");
                MainActivity.this.startActivity(intent);
            }
        });

        // button: create ac universal remote controller
        Button btnCreateACUniversalRemote = (Button) findViewById(R.id.button_create_ac_universal_remote);
        btnCreateACUniversalRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateACUniversalRemoteActivity.class);
                intent.putExtra("type_id", "2");
                intent.putExtra("brand_id", "1449");
                MainActivity.this.startActivity(intent);
            }
        });

        // button: create ac picker
        Button btnRunACPicker = (Button) findViewById(R.id.button_run_ac_picker);
        btnRunACPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateACPickerActivity.class);
                intent.putExtra("type_id", "2");
                intent.putExtra("brand_id", "1449");
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initializeSDK() {

        // initialize the SDK
        IRAPI.init(BOMEANS_SDK_API_KEY, getApplicationContext());

        // select server if needed
        IRAPI.switchToChineseServer(true);

        // set up the IR Blaster hardware data handling
        IRAPI.setCustomerIrBlaster(new MyIrBlaster());

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
                    Log.d(DBG_TAG, String.format("Brand: type=%s, id=%s, name_en=%s, name=%s",
                            typeInfo.typeId,
                            brand.brandId, brand.brandNameEN, brand.brandNameLocalized));
                }

                // iterate through all remotes
                // [warning] don't actually do this, system might determine that
                // you are abusing the service and disable the provided api key.
                for (BrandInfo brandInfo : brandList) {
                    getRemoteList(typeInfo, brandInfo);
                }

            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("[ERROR] failed to get brand lists %s", typeInfo.typeNameLocalized));
            }
        });
    }

    private void getRemoteList(final TypeInfo typeInfo, final BrandInfo brandInfo) {

        IRAPI.getRemoteList(typeInfo.typeId, brandInfo.brandId, true, new IGetRemoteListCallback() {

            @Override
            public void onDataReceived(List<RemoteInfo> remoteList) {

                for (RemoteInfo remoteInfo : remoteList) {
                    Log.d(DBG_TAG, String.format("Remote: type=%s(%s), brand=%s(%s), id=%s : %s",
                            typeInfo.typeId, typeInfo.typeNameLocalized, brandInfo.brandId, brandInfo.brandNameLocalized,
                            remoteInfo.remoteId, remoteInfo.supportedModels));
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("ERROR] failed to get remote list"));
            }
        });
    }

}
