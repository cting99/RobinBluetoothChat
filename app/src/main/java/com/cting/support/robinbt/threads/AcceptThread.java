package com.cting.support.robinbt.threads;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import static com.cting.support.robinbt.BtConstants.MSG_ACCEPT_FAIL;
import static com.cting.support.robinbt.BtConstants.MSG_ACCEPT_SUCCESS;
import static com.cting.support.robinbt.BtConstants.MY_UUID;

public class AcceptThread extends BaseThread {
    public static String TAG = "cting/AcceptThread";
    private BluetoothServerSocket serverSocket;

    public AcceptThread(Handler handler) {
        super(handler);
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("bt_server", MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                BluetoothSocket bluetoothSocket = serverSocket.accept();
                if (bluetoothSocket != null) {
                    handler.obtainMessage(MSG_ACCEPT_SUCCESS, bluetoothSocket).sendToTarget();
                }
                break;
            } catch (IOException e) {
                Log.e(TAG, "run: serverSocket accept exception:" + e.getLocalizedMessage());
                handler.obtainMessage(MSG_ACCEPT_FAIL).sendToTarget();
                e.printStackTrace();
                break;
            }

        }
    }

    @Override
    public void cancel() {
        Log.i(TAG, "cancel: ");
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.i(TAG, "AcceptThread cancel: exception:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
