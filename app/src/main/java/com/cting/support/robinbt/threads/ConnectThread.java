package com.cting.support.robinbt.threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import static com.cting.support.robinbt.BtConstants.MSG_CONNECT_FAIL;
import static com.cting.support.robinbt.BtConstants.MSG_CONNECT_SUCCESS;
import static com.cting.support.robinbt.BtConstants.MY_UUID;


public class ConnectThread extends BaseThread {
    public static String TAG = "cting/ConnectThread";
    private BluetoothSocket btSocket;

    public ConnectThread(Handler handler, BluetoothDevice device) {
        super(handler);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.w(TAG, "create btSocket to service exception:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            btSocket.connect();
            handler.obtainMessage(MSG_CONNECT_SUCCESS, btSocket).sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "run: connect btSocket exception:" + e.getLocalizedMessage());
            handler.obtainMessage(MSG_CONNECT_FAIL).sendToTarget();
            e.printStackTrace();
            try {
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "run: btSocket close exception:" + e.getLocalizedMessage());
                e1.printStackTrace();
            }
        }
    }

    public void cancel() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
