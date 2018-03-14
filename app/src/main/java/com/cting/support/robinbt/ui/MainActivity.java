package com.cting.support.robinbt.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cting.support.robinbt.BtConstants;
import com.cting.support.robinbt.R;
import com.cting.support.robinbt.threads.AcceptThread;
import com.cting.support.robinbt.threads.BaseThread;
import com.cting.support.robinbt.threads.ConnectThread;
import com.cting.support.robinbt.threads.ReadWriteThread;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements DeviceListFragment.Callback, ChatFragment.Callback {

    public static final String TAG = "cting/MainActivity";

    public static final int REQUEST_CODE_DISCOVERABLE = 1;
    public static final String TAG_CHAT = "CHAT";
    public static final String TAG_LIST = "LIST";
    private static final int MSG_SHOW_LIST = 100;
    private static final int MSG_SHOW_CHAT = 200;
    private static final int MSG_SHOW_CONVERSATION = 300;

    private BluetoothAdapter mBtAdapter;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ReadWriteThread mReadWriteThread;

    DeviceListFragment mListFragment;
    ChatFragment mChatFragment;

    Handler mHandler = new BluetoothHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Log.i(TAG, "onCreate: bt not avaliable");
            finish();
            return;
        }

        mListFragment = new DeviceListFragment();
        mChatFragment = new ChatFragment();
        setFragment(mListFragment);

        setDiscoverable();
    }

    private void setFragment(Fragment fragment) {
        String tag = "";
        if (fragment == mListFragment) {
            tag = TAG_LIST;
            setTitle("device list");
        } else if (fragment == mChatFragment) {
            tag = TAG_CHAT;
//            setTitle(mBtAdapter.getName());
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frgment_container, fragment, tag)
                .commit();
    }

    public static final int MENU_ITEM_DISCOVERABLE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, MENU_ITEM_DISCOVERABLE, 0, "discoverable");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: " + item.getItemId());
        if (item.getItemId() == MENU_ITEM_DISCOVERABLE) {
            setDiscoverable();
        }
        return true;
    }

    private void setDiscoverable() {
        if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.i(TAG, "setDiscoverable: request discoverable");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_CHAT) != null) {
            setFragment(mListFragment);
            return;
        }
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelThread(mAcceptThread);
        mAcceptThread = new AcceptThread(mHandler);
        mAcceptThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        cancelThread(mAcceptThread);
        cancelThread(mConnectThread);
        cancelThread(mReadWriteThread);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ",resultCode=" + resultCode);
        switch (requestCode) {
            case REQUEST_CODE_DISCOVERABLE:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "REQUEST_DISCOVERABLE fail", Toast.LENGTH_SHORT).show();
                } else {
//                    mHandler.sendEmptyMessage(MSG_DISCOVERY_TIME);
                    Toast.makeText(this, resultCode + " seconds for discoverable", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //From ChatFragment
    @Override
    public void send(String message) {
        Log.i(TAG, "send: mReadWriteThread=" + mReadWriteThread);
        if (mReadWriteThread != null) {
            mReadWriteThread.write(message);
        }
    }

    //From DeviceListFragment
    @Override
    public void requestConnectDevice(BluetoothDevice device) {
        String title = device.getName();
        setTitle(title);
        mConnectThread = new ConnectThread(mHandler, device);
        mConnectThread.start();
    }


    class BluetoothHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BtConstants.MSG_ACCEPT_SUCCESS:
                    Log.i(TAG, "MSG_ACCEPT_SUCCESS");
//                    Toast.makeText(MainActivity.this, "accept success!", Toast.LENGTH_SHORT).show();
                    cancelThread(mReadWriteThread);
                    mReadWriteThread = new ReadWriteThread(mHandler, (BluetoothSocket) msg.obj);
                    mReadWriteThread.start();
                    setFragment(mChatFragment);
                    break;
                case BtConstants.MSG_ACCEPT_FAIL:
                    Log.i(TAG, "MSG_ACCEPT_FAIL");
//                    Toast.makeText(MainActivity.this, "accept fail!", Toast.LENGTH_SHORT).show();
                    cancelThread(mAcceptThread);
                    setFragment(mListFragment);
                    break;
                case BtConstants.MSG_CONNECT_SUCCESS:
                    Log.i(TAG, "MSG_CONNECT_SUCCESS");
                    cancelThread(mReadWriteThread);
                    mReadWriteThread = new ReadWriteThread(mHandler, (BluetoothSocket) msg.obj);
                    mReadWriteThread.start();
//                    Toast.makeText(MainActivity.this, "connect success!", Toast.LENGTH_SHORT).show();
                    setFragment(mChatFragment);
                    break;
                case BtConstants.MSG_CONNECT_FAIL:
                    Log.i(TAG, "MSG_CONNECT_FAIL");
//                    Toast.makeText(MainActivity.this, "connect fail!", Toast.LENGTH_SHORT).show();
                    cancelThread(mConnectThread);
                    setFragment(mListFragment);
                    break;
                case BtConstants.MSG_READ_SUCCESS:
                    String readContent = (String) msg.obj;
                    Log.i(TAG, "MSG_READ_SUCCESS:" + readContent);
                    mChatFragment.setText(readContent);

                    break;
                case BtConstants.MSG_READ_FAIL:
                    Log.i(TAG, "MSG_READ_FAIL");
//                    Toast.makeText(MainActivity.this, "read fail!", Toast.LENGTH_SHORT).show();
                    cancelThread(mReadWriteThread);
                    Toast.makeText(MainActivity.this, "lost connection", Toast.LENGTH_SHORT).show();
                    setFragment(mListFragment);
                    break;
                case BtConstants.MSG_WRITE_SUCCESS:
                    String writeContent = (String) msg.obj;
                    Log.i(TAG, "MSG_WRITE_SUCCESS:" + writeContent);
                    mChatFragment.setText(writeContent);
                    break;
                case BtConstants.MSG_WRITE_FAIL:
                    Log.i(TAG, "MSG_WRITE_FAIL");
                    cancelThread(mReadWriteThread);
                    Toast.makeText(MainActivity.this, "write fail!", Toast.LENGTH_SHORT).show();
                    setFragment(mListFragment);
                    break;
            }
        }
    }

    private void cancelThread(BaseThread thread) {
        if (thread != null) {
            thread.cancel();
        }
    }
}
