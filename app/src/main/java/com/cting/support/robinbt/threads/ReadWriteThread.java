package com.cting.support.robinbt.threads;


import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.cting.support.robinbt.BtConstants.MSG_READ_FAIL;
import static com.cting.support.robinbt.BtConstants.MSG_READ_SUCCESS;
import static com.cting.support.robinbt.BtConstants.MSG_WRITE_FAIL;
import static com.cting.support.robinbt.BtConstants.MSG_WRITE_SUCCESS;

public class ReadWriteThread extends BaseThread {
    public static final String TAG = "cting/ReadWriteThread";

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ReadWriteThread(Handler handler, BluetoothSocket socket) {
        super(handler);
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                int count = inputStream.read(buffer);
                String from = socket.getRemoteDevice().getName();
                String content = new String(buffer, 0, count);
                handler.obtainMessage(MSG_READ_SUCCESS, from + " : " + content).sendToTarget();
                Log.i(TAG, "run: read:" + content);
            } catch (IOException e) {
                Log.e(TAG, "run: read input stream exception:" + e.getLocalizedMessage());
                handler.obtainMessage(MSG_READ_FAIL).sendToTarget();
                e.printStackTrace();
                break;
            }
        }

    }

    public void write(String message) {
        try {
            Log.i(TAG, "write: " + message);
            outputStream.write(message.getBytes());
            handler.obtainMessage(MSG_WRITE_SUCCESS, "ME : " + message).sendToTarget();
        } catch (IOException e) {
            Log.i(TAG, "write: exception:" + e.getLocalizedMessage());
            handler.obtainMessage(MSG_WRITE_FAIL);
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.i(TAG, "ConnectionThread cancel: exception:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
