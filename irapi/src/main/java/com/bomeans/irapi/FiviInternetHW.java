package com.bomeans.irapi;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bomeans.IRKit.BIRIRBlaster;
import com.bomeans.IRKit.ConstValue;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by admin on 16/9/12.
 */
public class FiviInternetHW implements BIRIRBlaster,ConstValue {
    private static final String TAG = "sss";
    public static String coreid;

    Context mContext;
    ConsumerIrManager mCIR;
    public FiviInternetHW(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public int sendData(byte[] bytes) {
        transmitCloudData(coreid, bytes);
        Log.e(TAG, bytes.toString());
        Log.e(TAG, "sendData: 遠端");
    return ConstValue.BIROK;
    }
    @Override
    public int isConnection() {
        return ConstValue.BIROK;
    }

    private static int MAX_DATA_SIZE = 400;
    private static String mServerUrl = "http://api.openfivi.com:3000";
    // sample code snippet for FiVi cloud server

    // query device connection status
    public void queryDeviceStatus(String deviceId) {

        QueryDeviceStatusTask task = new QueryDeviceStatusTask();
        task.execute(deviceId);
    }
    // transmit IR data to device
    private void transmitCloudData(
            @NonNull String deviceId,
            @NonNull final byte[] dataBytes) {

//        if (deviceId.isEmpty() || dataBytes.length == 0) {
//            if (null != mCommandCallback) {
//                mCommandCallback.onCommandCompleted(false);
//            }
//            return;
//        }

        // TODO
        SendIRCommandTask task = new SendIRCommandTask();
        task.execute(deviceId, dataBytes);
    }

    private class QueryDeviceStatusTask extends AsyncTask<String, String, Boolean> {

        String mDeviceId = "";

        @Override
        protected Boolean doInBackground(String... strings) {

            mDeviceId = strings[0];
            if (null == mDeviceId || mDeviceId.length() == 0) {
                return false;
            }

            JSONObject jsonStatus = retrieveJsonPost(
                    mServerUrl + "/login",
                    getLoginQueryString(mDeviceId),
                    "application/json; charset=UTF-8");

            try {
                Boolean bSuccess = jsonStatus.getBoolean("success");
                Boolean bOnline = jsonStatus.getBoolean("online");

                return bSuccess && bOnline;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

//            if (null != mDeviceStatusCallback) {
//                if (aBoolean) {
//                    mDeviceStatusCallback.onDeviceConnected(mDeviceId);
//                } else {
//                    mDeviceStatusCallback.onDeviceDisconnected(mDeviceId);
//                }
//            }
        }
    }


    private class SendIRCommandTask extends AsyncTask<Object, String, Boolean> {

        @Override
        protected Boolean doInBackground(Object... objects) {

            if (objects.length < 2) {
                return false;
            }

            if (!(objects[0] instanceof  String)) {
                return false;
            }

            if (!(objects[1] instanceof byte[])) {
                return false;
            }

            String deviceId = (String) objects[0];
            byte[] dataBytes = (byte[]) objects[1];

            if (null == deviceId || deviceId.length() == 0) {
                return false;
            }

            if (dataBytes == null || dataBytes.length == 0) {
                return false;
            }

            JSONObject jsonStatus;

            // check data byte array size, separate into multiple packets if needed
            if (dataBytes.length > MAX_DATA_SIZE) {
                int bytesToSend = dataBytes.length;
                byte[] trimmedDataBytes;
                int tmpDataLength;
                boolean bIsLastFrame = false;
                while (bytesToSend > 0) {
                    tmpDataLength = (bytesToSend >= MAX_DATA_SIZE) ? MAX_DATA_SIZE : bytesToSend;
                    bIsLastFrame = (tmpDataLength == bytesToSend);

                    trimmedDataBytes = new byte[tmpDataLength];
                    System.arraycopy(dataBytes, dataBytes.length - bytesToSend, trimmedDataBytes, 0, tmpDataLength);

                    jsonStatus = retrieveJsonPost(
                            mServerUrl + "/ctrl",
                            getCommandString(deviceId, trimmedDataBytes, bIsLastFrame),
                            "application/json; charset=UTF-8");

                    try {
                        // make sure we got the return id (the returned id is not device id)
                    /*String returnCommandId = */jsonStatus.getString("id");

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                    bytesToSend -= trimmedDataBytes.length;
                }
            } else {

                jsonStatus = retrieveJsonPost(
                        mServerUrl + "/ctrl",
                        getCommandString(deviceId, dataBytes),
                        "application/json; charset=UTF-8");

                try {
                /*String returnCommandId = */jsonStatus.getString("id");
                    return true;//returnedDeviceId.equalsIgnoreCase(deviceId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

//            if (null != mCommandCallback) {
//                mCommandCallback.onCommandCompleted(success);
//            }
        }
    }

    private String getLoginQueryString(String deviceId) {
        return String.format("{\"coreid\":\"%s\"}", deviceId);
    }

    private String getCommandString(String deviceId, byte[] dataBytes) {
        return getCommandString(deviceId, dataBytes, true);
    }

    private String getCommandString(String deviceId, byte[] dataBytes, boolean bIsLastFrame) {

        //{"coreid":"C6BEF17EE1600001B2EF1520A7FF1A12","order":{"Flag":"1","Data":"FF61002B00"}}

        StringBuilder dataString = new StringBuilder();
        for (byte data : dataBytes) {
            dataString.append(String.format("%02X", data));
        }

        String commandString = String.format("{\"coreid\":\"%s\",\"order\":{\"Flag\":\"%d\",\"Data\":\"%s\"}}",
                deviceId, bIsLastFrame ? 1 : 0, dataString.toString());

        return commandString;
    }
    public static JSONObject retrieveJsonPost(String requestURL, String postString, String contentTypeString) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            if (null != contentTypeString) {
                conn.setRequestProperty("Content-Type", contentTypeString);//"application/json; charset=UTF-8");
            }
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postString);

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }

                return new JSONObject(response);

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
