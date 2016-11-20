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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bomeans.irapi.IIRReader;
import com.bomeans.irapi.IIRReaderCallback;
import com.bomeans.irapi.IRAPI;

import java.util.Timer;
import java.util.TimerTask;

public class LearnAndSendActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private IIRReader mIrReader;
    private MyIrBlaster mMyIrBlaster;

    private Button mSendButton;
    private Button mLearnButton;
    private TextView mLearnResultText;
    private ProgressBar mProgressBar;
    private Timer mLearningTimer;

    private byte[] mLearnedDataForSending;  // ir signal data wrapping in Bomeans UART command format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_and_send);

        setTitle("Learn And Send");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get the IR Blaster hardware wrapper
        mMyIrBlaster = ((TestIRAPIApp)getApplication()).getMyIrBlaster();

        final ProgressBar initProgressBar = (ProgressBar) findViewById(R.id.init_progress_bar);
        IRAPI.createIRReader(getNew(), new IIRReaderCallback() {
            @Override
            public void onReaderCreated(IIRReader irReader) {
                initProgressBar.setVisibility(View.GONE);
                mIrReader = irReader;
            }

            @Override
            public void onReaderCreateFailed() {
                initProgressBar.setVisibility(View.GONE);
                Log.d(DBG_TAG, "ERROR]:failed to create ir reader!");
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLearnResultText = (TextView) findViewById(R.id.learn_result);

        mLearnButton = (Button) findViewById(R.id.learn_button);
        mLearnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sendLearningCommand()) {
                    mLearnResultText.setText("Waiting");
                } else {
                    mLearnResultText.setText("Please retry");
                }

                mSendButton.setEnabled(false);

                // simulate the receiving of ir learning data
                mMyIrBlaster.onDataArrived(new byte[]{
                        //(byte)0xFF,(byte)0x61,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x91,(byte)0x93,(byte)0xF0
                        (byte) 0xFF, (byte) 0x61, (byte) 0x00, (byte) 0x6C, (byte) 0x00, (byte) 0x92, (byte) 0xA6, (byte) 0x93, (byte) 0x56, (byte) 0x06, (byte) 0x26, (byte) 0x46, (byte) 0x4C, (byte) 0x23, (byte) 0x1C, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xB8, (byte) 0x11, (byte) 0x4C, (byte) 0x02, (byte) 0xB8, (byte) 0x06, (byte) 0x6E, (byte) 0x91, (byte) 0xF4, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x21, (byte) 0x31, (byte) 0x40, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x24, (byte) 0x00, (byte) 0x10, (byte) 0x22, (byte) 0x22, (byte) 0x12, (byte) 0x12, (byte) 0x22, (byte) 0x22, (byte) 0x12, (byte) 0x22, (byte) 0x12, (byte) 0x22, (byte) 0x22, (byte) 0x11, (byte) 0x21, (byte) 0x11, (byte) 0x11, (byte) 0x32, (byte) 0x34, (byte) 0x18, (byte) 0xF0
                });
            }
        });

        mSendButton = (Button) findViewById(R.id.transmit_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLearnedDataForSending != null) {

                    if (null != mIrReader) {
                        mIrReader.sendLearningData(mLearnedDataForSending);
                    }
                }
            }
        });
        mSendButton.setEnabled(false);
    }

    private Boolean sendLearningCommand() {
        if (null != mMyIrBlaster && mMyIrBlaster.isConnected()) {

            // show progress bar
            mProgressBar.setVisibility(View.GONE);
            if (null != mLearningTimer) {
                mLearningTimer.cancel();
            }

            mLearnedDataForSending = null;

            if (null != mIrReader) {

                mProgressBar.setVisibility(View.VISIBLE);
                mLearningTimer = new Timer(true);
                mLearningTimer.schedule(new LearningTimerTask(), 0, 1000);

                mIrReader.stopLearning();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mIrReader.startLearningAndGetData(IIRReader.PREFER_REMOTE_TYPE.Auto, new IIRReader.IIRReaderFormatMatchCallback() {
                    @Override
                    public void onFormatMatchSucceeded(final IIRReader.ReaderMatchResult formatMatchResult) {
                        LearnAndSendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String info = mLearnResultText.getText() + "\n";

                                if (formatMatchResult.isAc()) {
                                    info += String.format("AC: %s\n",
                                            formatMatchResult.formatId);
                                } else {
                                    info += String.format("TV: %s, C: 0x%X, K: 0x%X\n",
                                            formatMatchResult.formatId,
                                            formatMatchResult.customCode,
                                            formatMatchResult.keyCode);
                                }

                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onFormatMatchFailed(final IIRReader.FormatParsingErrorCode errorCode) {
                        LearnAndSendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String info = mLearnResultText.getText() + "\n";
                                switch (errorCode) {
                                    case LearningModeFailed:
                                        info += "Learning mode failed";
                                        break;
                                    case UnrecognizedFormat:
                                        info += "Un-recognized Format";
                                        break;
                                    case NoValidLearningData:
                                    default:
                                        info += "No Valid Learning Data";
                                        break;
                                }

                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onLearningDataReceived(byte[] learningData) {
                        mLearnedDataForSending = learningData;

                        final int waveCount = mIrReader.getWaveCount();
                        final int frequency = mIrReader.getFrequency();

                        LearnAndSendActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                mProgressBar.setVisibility(View.GONE);
                                mLearningTimer.cancel();
                                mSendButton.setEnabled(true);

                                String info = "Learn OK\n";
                                info += String.format("signal count: %d (%dHz)", waveCount, frequency);
                                mLearnResultText.setText(info);
                            }
                        });
                    }

                    @Override
                    public void onLearningDataFailed(final IIRReader.LearningErrorCode errorCode) {
                        LearnAndSendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mProgressBar.setVisibility(View.GONE);
                                mLearningTimer.cancel();
                                mSendButton.setEnabled(false);

                                switch (errorCode) {
                                    case LearningModeFailed:
                                        mLearnResultText.setText("Learning Mode Failed");
                                        break;
                                    case TimeOut:
                                        mLearnResultText.setText("Time Out");
                                        break;
                                    case IncorrectLearnedData:
                                    default:
                                        mLearnResultText.setText("Incorrect Data");
                                        break;
                                }
                            }
                        });
                    }
                });

                return true;
            } else {
                return false;
            }
        }
        return false;
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

    class LearningTimerTask extends TimerTask {

        int mSecCount = 0;

        @Override
        public void run() {
            mProgressBar.setProgress(mSecCount);
            mSecCount++;
            if (mSecCount > 15) {
                this.cancel();
            }
        }
    }

    private Boolean getNew() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getBoolean("get_new", false);
    }
}
