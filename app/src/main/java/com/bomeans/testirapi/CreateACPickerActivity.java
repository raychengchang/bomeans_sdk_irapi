package com.bomeans.testirapi;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.IGetRemoteListCallback;
import com.bomeans.irapi.IIRRemote;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.IRRemote;
import com.bomeans.irapi.RemoteInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateACPickerActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private Button mYesButton;
    private Button mNoButton;
    private Button mTestButton;
    private TextView mInfoText;

    private List<RemoteInfo> mRemoteInfoList;
    private Map<String, IIRRemote> mRemoteList = new HashMap<>();
    private int mCurrentIndex = 0;
    private IIRRemote mCurrentRemote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acpicker);

        setTitle("AC Picker Demo");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get parameters
        final String typeId = getIntent().getStringExtra("type_id");
        final String brandId = getIntent().getStringExtra("brand_id");

        mYesButton = (Button) findViewById(R.id.button_yes);
        mNoButton = (Button) findViewById(R.id.button_no);
        mTestButton = (Button) findViewById(R.id.button_key);
        mInfoText = (TextView) findViewById(R.id.info_text);

        enableButtons(false);

        Button restartButton = (Button) findViewById(R.id.button_restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = 0;
                showCurrentPicker();
            }
        });

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // we've done
                mInfoText.setText(String.format("Selected Remote: %s", mRemoteInfoList.get(mCurrentIndex)));
                enableButtons(false);
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to next remote
                mCurrentIndex++;
                if (mCurrentIndex >= mRemoteInfoList.size()) {
                    mInfoText.setText("Cannot find the match!");
                    enableButtons(false);
                    return;
                }

                showCurrentPicker();
            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCurrentRemote) {
                    mCurrentRemote.transmitIR("IR_ACKEY_POWER");
                }
            }
        });

        // run after the GUI has shown. Loading the all remotes of the brand might take a long time
        final View rootView = getWindow().getDecorView().getRootView();
        ViewTreeObserver observer = rootView .getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                IRAPI.getRemoteList(typeId, brandId, false, new IGetRemoteListCallback() {
                    @Override
                    public void onDataReceived(List<RemoteInfo> remoteList) {

                        mRemoteInfoList = remoteList;

                        startPicker(typeId, brandId);
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.d(DBG_TAG, String.format("ERROR]:%d failed to create ac picker", errorCode));
                    }
                });

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

    private void startPicker(final String typeId, final String brandId) {

        if (null == mRemoteInfoList) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mRemoteInfoList.size(); i++) {
                    final String remoteId = mRemoteInfoList.get(i).remoteId;
                    IRAPI.createRemote(typeId, brandId, remoteId, true, new ICreateRemoteCallback() {
                        @Override
                        public void onRemoteCreated(IRRemote remote) {
                            mRemoteList.put(remoteId, remote);
                            Log.d(DBG_TAG, String.format("Loaded: %s", remoteId));
                        }

                        @Override
                        public void onError(int errorCode) {
                            Log.d(DBG_TAG, String.format("ERROR]:%d failed to create ac remote %s",
                                    errorCode, remoteId));
                        }
                    });
                }
            }
        }).start();

        mCurrentIndex = 0;
        showCurrentPicker();
    }

    private void showCurrentPicker() {

        enableButtons(false);

        if (mCurrentIndex >= mRemoteInfoList.size()) {
            // no more remote, the picker failed to find the match
            mInfoText.setText("Failed to match!");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                RemoteInfo remoteInfo = mRemoteInfoList.get(mCurrentIndex);
                final String remoteId = remoteInfo.remoteId;

                // has the remote been downloaded?
                while (!mRemoteList.containsKey(remoteId)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!mRemoteList.containsKey(remoteId)) {

                    mCurrentRemote = null;

                    CreateACPickerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInfoText.setText(String.format("Time out for loading the remote %s", remoteId));
                            enableButtons(false);
                        }
                    });

                    return;

                } else {

                    mCurrentRemote = mRemoteList.get(remoteId);

                    CreateACPickerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInfoText.setText(String.format("%d/%d: %s", mCurrentIndex + 1, mRemoteInfoList.size(), remoteId));
                            enableButtons(true);
                        }
                    });

                }
            }
        }).start();

    }

    private void enableButtons(Boolean enabled) {
        mTestButton.setEnabled(enabled);
        mYesButton.setEnabled(enabled);
        mNoButton.setEnabled(enabled);
    }
}
