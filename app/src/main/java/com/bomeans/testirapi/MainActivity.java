package com.bomeans.testirapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bomeans.irapi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private List<TypeInfo> mTypeList = new ArrayList<>();
    private Map<TypeInfo, List<BrandInfo>> mBrands = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                intent.putExtra("brand_id", "13");
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
                intent.putExtra("brand_id", "1450");
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

        // button: learn and send
        Button btnLearnAndSend = (Button) findViewById(R.id.button_learn_n_send);
        btnLearnAndSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LearnAndSendActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        // button: learn and recognize
        Button btnLearnAndrecognize = (Button) findViewById(R.id.button_learn_n_recognize);
        btnLearnAndrecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LearnAndRecognizeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        // checkbox: get new
        final CheckBox chkGetNew = (CheckBox) findViewById(R.id.check_get_new);
        chkGetNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // save to SharedPreferences to share this setting cross Activities
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("get_new", chkGetNew.isChecked());
                editor.commit();
            }
        });
    }

    private void getAllTypes() {
        // get all supported types
        IRAPI.getTypeList("TW", getNew(), new IGetTypeListCallback() {
            @Override
            public void onDataReceived(List<TypeInfo> typeList) {
                mTypeList = typeList;
                /*
                for (TypeInfo type : typeList) {
                    Log.d(DBG_TAG, String.format("Type: id=%s, name_en=%s, name=%s", type.typeId, type.typeNameEN, type.typeNameLocalized));
                }*/

                // get all supported brands
                for (TypeInfo typeInfo : mTypeList) {
                    getAllBrands(typeInfo);
                    //getKeyNameList(typeInfo);
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("[ERROR]:%d failed to get type list.", errorCode));
            }
        });
    }

    private void getAllBrands(final TypeInfo typeInfo) {

        IRAPI.getBrandList("TW", typeInfo.typeId, true, getNew(), new IGetBrandListCallback() {

            @Override
            public void onDataReceived(List<BrandInfo> brandList) {

                mBrands.put(typeInfo, brandList);

                /*
                for (BrandInfo brand : brandList) {
                    Log.d(DBG_TAG, String.format("Brand: type=%s, id=%s, name_en=%s, name=%s",
                            typeInfo.typeId,
                            brand.brandId, brand.brandNameEN, brand.brandNameLocalized));
                }*/

                // iterate through all remotes
                // [warning] don't actually do this, system might determine that
                // you are abusing the service and disable the provided api key.
                for (BrandInfo brandInfo : brandList) {
                    getRemoteList(typeInfo, brandInfo);
                }

            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("[ERROR]:%d failed to get brand lists %s", errorCode, typeInfo.typeNameLocalized));
            }
        });
    }

    private void getRemoteList(final TypeInfo typeInfo, final BrandInfo brandInfo) {

        IRAPI.getRemoteList(typeInfo.typeId, brandInfo.brandId, getNew(), new IGetRemoteListCallback() {

            @Override
            public void onDataReceived(List<RemoteInfo> remoteList) {

                Log.d(DBG_TAG, String.format("%s: %s: %d",
                        typeInfo.typeNameEN, brandInfo.brandNameEN, remoteList.size()));
                for (RemoteInfo remoteInfo : remoteList) {
                    Log.d(DBG_TAG, String.format("Remote: type=%s(%s), brand=%s(%s), id=%s : %s",
                            typeInfo.typeId, typeInfo.typeNameLocalized, brandInfo.brandId, brandInfo.brandNameLocalized,
                            remoteInfo.remoteId, remoteInfo.supportedModels));
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("ERROR]:%d failed to get remote list", errorCode));
            }
        });
    }

    private void getKeyNameList(final TypeInfo typeInfo) {

        IRAPI.getAvailableKeyList(typeInfo.typeId, "TW", getNew(), new IGetAvailableKeyListCallback() {
            @Override
            public void onDataReceived(List<KeyInfo2> keyList) {
                for (KeyInfo2 keyInfo : keyList) {
                    Log.d(DBG_TAG, String.format(
                            "Key: type=%s, %s (%s)",
                            typeInfo.typeNameLocalized, keyInfo.keyId, keyInfo.keyNameLocalized));
                }
            }

            @Override
            public void onError(int errorCode) {
                Log.d(DBG_TAG, String.format("ERROR]:%d failed to get key list", errorCode));
            }
        });
    }

    private Boolean getNew() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getBoolean("get_new", false);
    }
}
