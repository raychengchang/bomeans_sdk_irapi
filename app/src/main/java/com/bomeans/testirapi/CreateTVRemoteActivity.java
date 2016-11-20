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

public class CreateTVRemoteActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tvremote);

        setTitle("TV Demo");

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
        String remoteId = getIntent().getStringExtra("remote_id");

        if ((null != typeId) && (null != brandId) && (null != remoteId)) {
            IRAPI.createRemote(typeId, brandId, remoteId, getNew(), new ICreateRemoteCallback() {
                @Override
                public void onRemoteCreated(final IRRemote remote) {

                    progressBar.setVisibility(View.GONE);

                    // get all keys in this remote
                    String[] keyList = remote.getKeyList();

                    LinearLayout layout = new LinearLayout(CreateTVRemoteActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    for (int i = 0; i < keyList.length; i++) {
                        final String keyId = keyList[i];
                        Button keyButton = new Button(CreateTVRemoteActivity.this);
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
                    Log.d(DBG_TAG, String.format("ERROR]:%d failed to create remote controller", errorCode));
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
