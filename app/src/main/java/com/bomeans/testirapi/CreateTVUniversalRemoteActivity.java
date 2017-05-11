package com.bomeans.testirapi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.IRRemote;

public class CreateTVUniversalRemoteActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tvuniversal_remote);

        setTitle("TV Universal Demo");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ScrollView panel = (ScrollView) findViewById(R.id.scroll_view);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // get parameters
        String typeId = getIntent().getStringExtra("type_id");
        String brandId = getIntent().getStringExtra("brand_id");

        if ((null != typeId) && (null != brandId)) {

            // Two kinds of universal remote controllers for TV-like remote controllers.
            // You can call IRAPI.createSimplifiedUniversalRemote() to create a universal remote controller
            // with only common keys in all underlying remote controllers.
            // or call IRAPI.createFullUniversalRemote() to create one with all possible keys
            // in the underlying remote controllers.
            // Full universal remote controller is recommended.
            IRAPI.createFullUniversalRemote(typeId, brandId, getNew(), new ICreateRemoteCallback() {
                @Override
                public void onRemoteCreated(final IRRemote remote) {

                    progressBar.setVisibility(View.GONE);

                    // set the repeat count of the transmitted IR.
                    // if set to 0, only the 1st frame (called normal-frame) is transmitted,
                    // set to 1 (default), 1 normal-frame + 1 repeat-frame is transmitted.
                    remote.setRepeatCount(2);

                    // get all keys in this remote
                    String[] keyList = remote.getKeyList();

                    // machine models
                    String[] machineModels = remote.getModels();
                    Log.d(DBG_TAG, "supported machine models:");
                    for (String model : machineModels) {
                        Log.d(DBG_TAG, model);
                    }

                    LinearLayout layout = new LinearLayout(CreateTVUniversalRemoteActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    for (int i = 0; i < keyList.length; i++) {
                        final String keyId = keyList[i];
                        Button keyButton = new Button(CreateTVUniversalRemoteActivity.this);
                        keyButton.setText(keyId);
                        keyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                remote.transmitIR(keyId);
                            }
                        });

                        layout.addView(keyButton);
                    }

                    panel.addView(layout);
                }

                @Override
                public void onError(int errorCode) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(DBG_TAG, String.format("ERROR]:%d failed to create univeral remote controller", errorCode));
                }
            });
        }
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

    private Boolean getNew() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getBoolean("get_new", false);
    }
}
