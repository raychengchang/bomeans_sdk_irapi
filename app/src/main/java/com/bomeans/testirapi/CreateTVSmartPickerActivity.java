package com.bomeans.testirapi;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.ICreateSmartPickerCallback;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.SmartPickerResult;
import com.bomeans.irapi.TVSmartPicker;

public class CreateTVSmartPickerActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private TVSmartPicker mSmartPicker;

    private Button mYesButton;
    private Button mNoButton;
    private Button mTestButton;
    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tvsmart_picker);

        setTitle("TV Smart Picker Demo");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get parameters
        String typeId = getIntent().getStringExtra("type_id");
        String brandId = getIntent().getStringExtra("brand_id");

        mYesButton = (Button) findViewById(R.id.button_yes);
        mNoButton = (Button) findViewById(R.id.button_no);
        mTestButton = (Button) findViewById(R.id.button_key);
        mInfoText = (TextView) findViewById(R.id.info_text);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        enableButtons(false);

        Button restartButton = (Button) findViewById(R.id.button_restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSmartPicker) {

                    // start over the smart picker
                    mSmartPicker.reset();
                    startSmartPicker();
                }
            }
        });

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSmartPicker) {
                    mSmartPicker.setPickerResult(true);
                    if (mSmartPicker.isPickerCompleted()) {
                        SmartPickerResult[] results = mSmartPicker.getPickerResult();

                        String info = String.format("%d remotes matched: ", results.length);
                        for (SmartPickerResult result : results) {
                            info += String.format("%s, ", result.remoteId);
                        }
                        mInfoText.setText(info);

                        enableButtons(false);
                    } else {
                        String nextKeyId = mSmartPicker.getPickerKey();
                        mTestButton.setText(nextKeyId);
                    }
                }
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSmartPicker) {
                    mSmartPicker.setPickerResult(false);
                    if (mSmartPicker.isPickerCompleted()) {
                        Toast.makeText(
                                CreateTVSmartPickerActivity.this, "No Match!", Toast.LENGTH_SHORT)
                                .show();

                        enableButtons(false);
                    } else {
                        String nextKeyId = mSmartPicker.getPickerKey();
                        mTestButton.setText(nextKeyId);
                    }
                }
            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSmartPicker) {
                    mSmartPicker.transmitIR();
                }
            }
        });

        IRAPI.createSmartPicker(typeId, brandId, false, new ICreateSmartPickerCallback() {
            @Override
            public void onPickerCreated(TVSmartPicker smartPicker) {

                progressBar.setVisibility(View.GONE);

                mSmartPicker = smartPicker;
                startSmartPicker();
            }

            @Override
            public void onError(int errorCode) {
                progressBar.setVisibility(View.GONE);
                Log.d(DBG_TAG, String.format("ERROR]:%d failed to create smart picker", errorCode));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSmartPicker() {
        if (null == mSmartPicker) {
            return;
        }

        mInfoText.setText("");

        mTestButton.setText(mSmartPicker.getPickerKey());
        enableButtons(true);
    }

    private void enableButtons(Boolean enabled) {
        mTestButton.setEnabled(enabled);
        mYesButton.setEnabled(enabled);
        mNoButton.setEnabled(enabled);
    }

}
