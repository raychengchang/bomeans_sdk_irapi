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

import com.bomeans.irapi.ACKeyOptions;
import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.IRRemote;

import java.util.ArrayList;
import java.util.List;

public class CreateACUniversalRemoteActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private IRRemote mMyAcRemote = null;

    // these are keys for univeral AC remote controller
    // power
    Boolean mHasPowerKey = true;
    TextView mCurrentPowerText;
    Button mPowerButton;
    // temp +/-
    Boolean mHasTemperatureKey = true;
    TextView mCurrentTempText;
    Button mTempUpButton;
    Button mTempDownButton;
    // mode
    Boolean mHasModeKey = true;
    TextView mCurrentModeText;
    Button mModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acuniversal_remote);

        setTitle("AC Universal Demo");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get parameters
        String typeId = getIntent().getStringExtra("type_id");
        String brandId = getIntent().getStringExtra("brand_id");

        mCurrentPowerText = (TextView) findViewById(R.id.current_power);
        mPowerButton = (Button) findViewById(R.id.button_power);

        mCurrentTempText = (TextView) findViewById(R.id.current_temp);
        mTempUpButton = (Button) findViewById(R.id.button_temp_up);
        mTempDownButton = (Button) findViewById(R.id.button_temp_down);

        mCurrentModeText = (TextView) findViewById(R.id.current_mode);
        mModeButton = (Button) findViewById(R.id.button_mode);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        IRAPI.createSimplifiedUniversalRemote(typeId, brandId, false, new ICreateRemoteCallback() {
            @Override
            public void onRemoteCreated(IRRemote remote) {
                progressBar.setVisibility(View.GONE);

                mMyAcRemote = remote;

                CreateACUniversalRemoteActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        createMyGUI();
                    }
                });
            }

            @Override
            public void onError(int errorCode) {
                progressBar.setVisibility(View.GONE);
                Log.d(DBG_TAG, String.format("ERROR]:%d failed to create ac universal remote", errorCode));
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

    private void createMyGUI() {
        if (null == mMyAcRemote) {
            return;
        }

        String[] allSupportedKeys = mMyAcRemote.getKeyList();

        // power key
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_POWER", allSupportedKeys);
            mPowerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        // get the power state
                        ACKeyOptions currentKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_POWER");

                        // passing null will cycle the internal state, or you can just call
                        // transmit("IR_ACKEY_POWER") which is equivalent to passing null in option id.
                        mMyAcRemote.transmitIR("IR_ACKEY_POWER", null);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasPowerKey = false;
            mPowerButton.setEnabled(false);
        }

        // do we have temp key?
        if (containString("IR_ACKEY_TEMP", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_TEMP", allSupportedKeys);
            mTempUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {

                        // get the current temp
                        ACKeyOptions currentKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
                        if (currentKeyOptions.currentOption < currentKeyOptions.options.length - 1) {  // not yet reach the end
                            mMyAcRemote.transmitIR("IR_ACKEY_TEMP", null);

                            // update GUI
                            updateGUI();
                        }
                    }
                }
            });

            mTempDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the current temp
                    ACKeyOptions currentKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
                    if (currentKeyOptions.currentOption > 0) {  // not yet reach the front
                        String nextTempOptionId = currentKeyOptions.options[currentKeyOptions.currentOption - 1];
                        mMyAcRemote.transmitIR("IR_ACKEY_TEMP", nextTempOptionId);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasTemperatureKey = false;
            mTempUpButton.setEnabled(false);
            mTempDownButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_MODE", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_MODE", allSupportedKeys);
            mModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_MODE", null);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasModeKey = false;
            mModeButton.setEnabled(false);
        }

        updateGUI();
    }

    private void updateGUI() {

        String[] allSupportedKeys = mMyAcRemote.getKeyList();

        // power
        if (containString("IR_ACKEY_POWER", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_POWER");
            showPowerText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // temp
        if (containString("IR_ACKEY_TEMP", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_TEMP");
            showTempText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // mode
        if (containString("IR_ACKEY_MODE", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_MODE");
            showModeText(newKeyOptions.options[newKeyOptions.currentOption]);
        }
    }

    private String extractTempStringFromOptionString(String tempOptionString) {

        /* for the temperature, the option could be
            (a) IR_ACOPT_TEMP_P1, _0, _N1 which represents +1/0/-1 for adjusting limited
            temperature range in auto mode for some specific AC remote controllers.
            (b) IR_ACSTATE_TEMP_XX which is normal temperature degrees.
        */
        if (tempOptionString.startsWith("IR_ACOPT_TEMP_")) {
            return tempOptionString.substring("IR_ACOPT_TEMP_".length());
        } else if (tempOptionString.startsWith("IR_ACSTATE_TEMP_")) {
            return tempOptionString.substring("IR_ACSTATE_TEMP_".length());
        }

        return "";
    }

    // power key GUI
    private void showPowerText(String currentPowerOption) {
        if (currentPowerOption.contains("IR_ACOPT_POWER_")) {
            String powerString = currentPowerOption.substring("IR_ACOPT_POWER_".length());
            mCurrentPowerText.setText(powerString);

            // for the AC universal remote controller, we assume it's the type of ValidWhilePoweredOn
            Boolean isOn = !powerString.equalsIgnoreCase("OFF");

            if (mMyAcRemote.acGetGuiFeatures() != null) {
                switch (mMyAcRemote.acGetGuiFeatures().displayMode) {
                    case ValidWhilePoweredOn:  // has normal display (display on when power on, off when power off
                        if (powerString.equalsIgnoreCase("OFF")) {
                            isOn = false;
                        } else {
                            isOn = true;
                        }
                        break;

                    case NoDisplay:   // no display
                        // this type of remote controller does not have LCD display.
                        isOn = false;
                        break;

                    case AlwaysOn:   // has always on display
                        // this kind of remote controller does not maintain the power on/off state,
                        // power on/off sends out the same IR signal (so it's toggle type). Thus the power
                        // state is maintained by the air conditioner itself, not in the remote controller.
                        isOn = true;
                        break;
                }
            }

            /*
                disable all key buttons (except the power key) if the display is off
                (so it's power off state, not allow keys to be pressed)

                Note: in power off state, the remote controller can still send out IR signals, but
                unless the signal contains power-on command (the pressed key is POWER key),
                the air-conditioner will receive the IR signal but not doing anything (won't power on)

                So you should disable the the keys when power is off.
             */
            mCurrentTempText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mTempUpButton.setEnabled(mHasTemperatureKey ? isOn : false);
            mTempDownButton.setEnabled(mHasTemperatureKey ? isOn : false);
            mCurrentModeText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mModeButton.setEnabled(mHasModeKey ? isOn : false);
        }
    }

    // temperature key GUI
    private void showTempText(String currentTempOption) {

        String tempString = extractTempStringFromOptionString(currentTempOption);

        try {
            int temp = Integer.parseInt(tempString);
            mCurrentTempText.setText(Integer.toString(temp));
        } catch (Exception e) {
            if (tempString.startsWith("P1")) {
                mCurrentTempText.setText(tempString.replace("P", "+"));
            } else if (tempString.startsWith("N")) {
                mCurrentTempText.setText(tempString.replace("N", "-"));
            } else {
                mCurrentTempText.setText(tempString);
            }
        }
    }

    // mode key GUI
    private void showModeText(String currentModeOption) {
        if (currentModeOption.contains("IR_ACOPT_MODE_")) {
            String modeString = currentModeOption.substring("IR_ACOPT_MODE_".length());
            mCurrentModeText.setText(modeString);
        }
    }

    private Boolean containString(String targetString, String[] stringArray) {
        for(String srcString : stringArray) {
            if (targetString.equalsIgnoreCase(srcString)) {
                return true;
            }
        }

        return false;
    }

    private String[] removeString(String targetString, String[] stringArray) {
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            if (!targetString.equalsIgnoreCase(stringArray[i])) {
                newList.add(stringArray[i]);
            }
        }

        return newList.toArray(new String[newList.size()]);
    }
}
