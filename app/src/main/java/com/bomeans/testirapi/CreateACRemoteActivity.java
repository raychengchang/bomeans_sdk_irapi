package com.bomeans.testirapi;

import android.content.Context;
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
import android.widget.TextView;

import com.bomeans.irapi.ACKeyOptions;
import com.bomeans.irapi.ICreateRemoteCallback;
import com.bomeans.irapi.IRAPI;
import com.bomeans.irapi.IRRemote;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * For the AC Remote, keep track of the following key IDs and option IDs for the GUI mapping.
 *
 * IR_ACKEY_POWER
 *      - IR_ACOPT_POWER_ON
 *      - IR_ACOPT_POWER_OFF
 *
 * IR_ACKEY_MODE
 *      - IR_ACOPT_MODE_AUTO
 *      - IR_ACOPT_MODE_COOL
 *      - IR_ACOPT_MODE_WARM
 *      - IR_ACOPT_MODE_DRY
 *      - IR_ACOPT_MODE_FAN
 *
 * IR_ACKEY_AIRSWING_UD
 *      - IR_ACOPT_AIRSWING_UD_A    (Auto)
 *      - IR_ACOPT_AIRSWING_UD_OFF
 *      - IR_ACOPT_AIRSWING_UD_1    (1..8 is the flap position)
 *      - IR_ACOPT_AIRSWING_UD_2
 *      - IR_ACOPT_AIRSWING_UD_3
 *      - IR_ACOPT_AIRSWING_UD_4
 *      - IR_ACOPT_AIRSWING_UD_5
 *      - IR_ACOPT_AIRSWING_UD_6
 *      - IR_ACOPT_AIRSWING_UD_7
 *      - IR_ACOPT_AIRSWING_UD_8
 *
 * IR_ACKEY_AIRSWING_LR
 *      - IR_ACOPT_AIRSWING_LR_A    (Auto)
 *      - IR_ACOPT_AIRSWING_LR_OFF
 *      - IR_ACOPT_AIRSWING_LR_1    (1..8 is the flap position)
 *      - IR_ACOPT_AIRSWING_LR_2
 *      - IR_ACOPT_AIRSWING_LR_3
 *      - IR_ACOPT_AIRSWING_LR_4
 *      - IR_ACOPT_AIRSWING_LR_5
 *      - IR_ACOPT_AIRSWING_LR_6
 *      - IR_ACOPT_AIRSWING_LR_7
 *      - IR_ACOPT_AIRSWING_LR_8
 *
 * IR_ACKEY_FANSPEED
 *      - IR_ACOPT_FANSPEED_A   (Auto)
 *      - IR_ACOPT_FANSPEED_L   (Low)
 *      - IR_ACOPT_FANSPEED_M   (Middle)
 *      - IR_ACOPT_FANSPEED_H   (High)
 *      - IR_ACOPT_FANSPEED_H1  (High 1)
 *      - IR_ACOPT_FANSPEED_H2  (High 2)
 *      - IR_ACOPT_FANSPEED_H3  (High 3)
 *
 *  IR_ACKEY_AIRSWAP
 *      - IR_ACOPT_AIRSWAP_ON
 *      - IR_ACOPT_AIRSWAP_OFF
 *      - IR_ACOPT_AIRSWAP_1
 *      - IR_ACOPT_AIRSWAP_2
 *      - IR_ACOPT_AIRSWAP_3
 *
 *  All other AC keys can be found by calling IRAPI.getAvailableKeyList() with
 *  AC type as the inpit parameter
 */
public class CreateACRemoteActivity extends AppCompatActivity {

    private String DBG_TAG = "IRAPI";

    private IRRemote mMyAcRemote = null;

    private String mAcRemoteId;

    // GUI: fixed buttons (common for most air-conditioners)
    // These are most common keys for AC remote controllers.
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
    // fan-speed
    Boolean mHasFanSpeedKey = true;
    TextView mCurrentFanSpeedText;
    Button mFanSpeedButton;
    // air-sweep
    Boolean mHasVerticalAirSwingKey = true;
    Boolean mHasHorizontalAirSwingKey = true;
    TextView mCurrentVerticalAirSwingText;
    TextView mCurrentHorizontalAirSwingText;
    Button mVerticalAirSwingButton;
    Button mHorizontalAirSwingButton;
    // others
    LinearLayout mExtraKeyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acremote);

        setTitle("AC Demo");

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get parameters
        String typeId = getIntent().getStringExtra("type_id");
        String brandId = getIntent().getStringExtra("brand_id");
        mAcRemoteId = getIntent().getStringExtra("remote_id");

        mCurrentPowerText = (TextView) findViewById(R.id.current_power);
        mPowerButton = (Button) findViewById(R.id.button_power);

        mCurrentTempText = (TextView) findViewById(R.id.current_temp);
        mTempUpButton = (Button) findViewById(R.id.button_temp_up);
        mTempDownButton = (Button) findViewById(R.id.button_temp_down);

        mCurrentModeText = (TextView) findViewById(R.id.current_mode);
        mModeButton = (Button) findViewById(R.id.button_mode);

        mCurrentFanSpeedText = (TextView) findViewById(R.id.current_fan_speed);
        mFanSpeedButton = (Button) findViewById(R.id.button_fanspeed);

        mCurrentVerticalAirSwingText = (TextView) findViewById(R.id.current_air_ud);
        mVerticalAirSwingButton = (Button) findViewById(R.id.button_air_ud);
        mCurrentHorizontalAirSwingText = (TextView) findViewById(R.id.current_air_lr);
        mHorizontalAirSwingButton = (Button) findViewById(R.id.button_air_lr);

        mExtraKeyLayout = (LinearLayout) findViewById(R.id.extend_keys);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        IRAPI.createRemote(typeId, brandId, mAcRemoteId, getNew(), new ICreateRemoteCallback() {
            @Override
            public void onRemoteCreated(IRRemote remote) {

                progressBar.setVisibility(View.GONE);

                mMyAcRemote = remote;

                // try to restore the ac state
                byte[] acStateData = getAcState(mAcRemoteId);
                if (acStateData != null) {
                    mMyAcRemote.acSetStateData(acStateData);
                } else {
                    // maybe we can set the initial state of the remote if no previous saved state?
                    // to do so, call mMyAcRemote.acSetKeyOption()
                    mMyAcRemote.acSetKeyOption("IR_ACKEY_MODE", "IR_ACOPT_MODE_COOL");
                    mMyAcRemote.acSetKeyOption("IR_ACKEY_TEMP", "IR_ACSTATE_TEMP_25");
                }

                CreateACRemoteActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createMyGUI();
                    }
                });
            }

            @Override
            public void onError(int errorCode) {
                progressBar.setVisibility(View.GONE);
                Log.d(DBG_TAG, String.format("ERROR]:%d failed to create ac remote", errorCode));
            }
        });
    }

    @Override
    protected void onPause() {
        if (null != mMyAcRemote) {
            byte[] acStateData = mMyAcRemote.acGetStateData();
            if (null != acStateData) {
                saveAcState(mAcRemoteId, acStateData);
            }
        }

        super.onPause();
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

        if (containString("IR_ACKEY_FANSPEED", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_FANSPEED", allSupportedKeys);
            mFanSpeedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_FANSPEED", null);

                        // update GUI
                        updateGUI();
                    }
                }
            });
        } else {
            mHasFanSpeedKey = false;
            mFanSpeedButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_AIRSWING_LR", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_AIRSWING_LR", allSupportedKeys);
            mHorizontalAirSwingButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_AIRSWING_LR", null);

                        updateGUI();
                    }
                }
            });
        } else {
            mHasHorizontalAirSwingKey = false;
            mHorizontalAirSwingButton.setEnabled(false);
        }

        if (containString("IR_ACKEY_AIRSWING_UD", allSupportedKeys)) {
            allSupportedKeys = removeString("IR_ACKEY_AIRSWING_UD", allSupportedKeys);
            mVerticalAirSwingButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != mMyAcRemote) {
                        mMyAcRemote.transmitIR("IR_ACKEY_AIRSWING_UD", null);

                        updateGUI();
                    }
                }
            });
        } else {
            mHasVerticalAirSwingKey = false;
            mVerticalAirSwingButton.setEnabled(false);
        }

        createNonFixedKeysGUI(allSupportedKeys);

        updateGUI();
    }

    private void createNonFixedKeysGUI(String[] keyIDs) {

        mExtraKeyLayout.removeAllViews();

        // handle all the "extension keys" here.
        // extension keys are those not in common keys of remote controllers.
        for (int i = 0; i < keyIDs.length; i++) {

            final String keyId = keyIDs[i];

            Button button = new Button(this);
            button.setText(keyIDs[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyAcRemote.transmitIR(keyId, null);
                }
            });

            mExtraKeyLayout.addView(button);
        }
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

        // fanspeed
        if (containString("IR_ACKEY_FANSPEED", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_FANSPEED");
            showFanSpeedText(newKeyOptions.options[newKeyOptions.currentOption]);
        }

        // air-swing
        if (containString("IR_ACKEY_AIRSWING_LR", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_AIRSWING_LR");
            showAirSwingText(newKeyOptions.options[newKeyOptions.currentOption]);
        }
        if (containString("IR_ACKEY_AIRSWING_UD", allSupportedKeys)) {
            ACKeyOptions newKeyOptions = mMyAcRemote.acGetKeyOption("IR_ACKEY_AIRSWING_UD");
            showAirSwingText(newKeyOptions.options[newKeyOptions.currentOption]);
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

            Boolean isOn = true;
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
            mCurrentFanSpeedText.setVisibility(isOn ? View.VISIBLE: View.INVISIBLE);
            mFanSpeedButton.setEnabled(mHasFanSpeedKey ? isOn : false);
            mCurrentHorizontalAirSwingText.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
            mHorizontalAirSwingButton.setEnabled(mHasHorizontalAirSwingKey ? isOn : false);
            mCurrentVerticalAirSwingText.setVisibility(isOn ? View.VISIBLE : View.INVISIBLE);
            mVerticalAirSwingButton.setEnabled(mHasVerticalAirSwingKey ? isOn : false);

            for (int i = 0; i < mExtraKeyLayout.getChildCount(); i++) {
                mExtraKeyLayout.getChildAt(i).setEnabled(isOn);
            }
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

    // fan-speed key GUI
    private void showFanSpeedText(String currentFanOption) {

        if (currentFanOption.contains("IR_ACOPT_FANSPEED_")) {
            String fanString = currentFanOption.substring("IR_ACOPT_FANSPEED_".length());

            // map the ID into specific string (or icon)
            if (fanString.equalsIgnoreCase("A")) {
                mCurrentFanSpeedText.setText("Auto");
            } else if (fanString.equalsIgnoreCase("L")) {
                mCurrentFanSpeedText.setText("Low");
            } else if (fanString.equalsIgnoreCase("M")) {
                mCurrentFanSpeedText.setText("Middle");
            } else if (fanString.equalsIgnoreCase("H")) {
                mCurrentFanSpeedText.setText("High");
            } else {
                mCurrentFanSpeedText.setText(fanString);
            }
        }
    }

    // air-swing key GUI
    private void showAirSwingText(String currentOption) {
        if (currentOption.contains("IR_ACOPT_AIRSWING_LR_")) {
            mCurrentHorizontalAirSwingText.setText(
                    currentOption.substring("IR_ACOPT_AIRSWING_LR_".length()));
        } else if (currentOption.contains("IR_ACOPT_AIRSWING_UD_")) {
            mCurrentVerticalAirSwingText.setText(
                    currentOption.substring("IR_ACOPT_AIRSWING_UD_".length()));
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

    private Boolean saveAcState(String remoteId, byte[] acStateData) {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ac_remote_id", remoteId);
        editor.commit();

        try {
            File file = new File(this.getFilesDir(), "acSingleState");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(acStateData);
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private byte[] getAcState(String remoteId) {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String savedRemoteId = sharedPref.getString("ac_remote_id", "");
        if (savedRemoteId != null && savedRemoteId.equalsIgnoreCase(remoteId)) {

            File file = new File(this.getFilesDir(), "acSingleState");
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Boolean getNew() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getBoolean("get_new", false);
    }
}
