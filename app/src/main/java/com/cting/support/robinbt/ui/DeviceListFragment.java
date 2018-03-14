package com.cting.support.robinbt.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.cting.support.robinbt.R;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by cting on 2018/3/14.
 */

public class DeviceListFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "cting/ListFragment";

    @BindView(R.id.scrollView2)
    ScrollView scrollView;
    @BindView(R.id.scan_btn)
    Button searchBtn;
    @BindView(R.id.container)
    LinearLayout container;
    Unbinder unbinder;
    private BluetoothAdapter mBtAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.i(TAG, "onReceive: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    Log.i(TAG, "onReceive: found:" + device.getName() + "," + device.getAddress());
                    addItem(device);
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i(TAG, "onReceive: ACTION_DISCOVERY_STARTED-------");
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "onReceive: ACTION_DISCOVERY_STARTED-------");
            }

        }
    };

    public DeviceListFragment() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container,false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);
        findPairedDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }

    @OnClick(R.id.scan_btn)
    public void onViewClicked() {
        Log.i(TAG, "onViewClicked: start discovery");
        startSearch();
    }

/*
    private void findDevice() {
        findPairedDevice();
        startSearch();
    }*/

    private void findPairedDevice() {
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        if (bondedDevices != null && bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                Log.i(TAG, "findPairedDevice: " + device.getName() + "," + device.getAddress());
                addItem(device);
            }
        }
    }

    private void startSearch() {
        boolean ret = mBtAdapter.startDiscovery();
        container.removeAllViews();
        addressList.clear();
        Log.i(TAG, "startSearch: " + ret);
        showProgress();
    }

    private void stopSearch() {
        Log.i(TAG, "stopSearch: cancel discovery");
        mBtAdapter.cancelDiscovery();
    }

    private ProgressDialog mProgressBar;
    private void showProgress() {
        if (mProgressBar == null) {
            mProgressBar = new ProgressDialog(getActivity());
            mProgressBar.setTitle("Search");
            mProgressBar.setMessage("touch to cancel");
            mProgressBar.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    stopSearch();
                }
            });
        }
        if (!mProgressBar.isShowing()) {
            mProgressBar.show();
        }
    }

    ArrayList<String> addressList = new ArrayList<>();

    private void addItem(BluetoothDevice device) {
        Button item = new Button(getActivity());
        String name = device.getName();
        String address = device.getAddress();
        if (addressList.contains(address)) {
            return;
        }
        addressList.add(address);
        if (TextUtils.isEmpty(name)) {
            item.setText(address);
        } else {
            item.setText(name);
        }
        item.setTag(address);
        item.setOnClickListener(this);
        container.addView(item);
    }


    @Override
    public void onClick(View v) {
        final String address = (String) v.getTag();
        Log.i(TAG, "onClick: " + address);

        if (callback != null) {
            callback.requestConnectDevice(mBtAdapter.getRemoteDevice(address));
        }
    }

    Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Log.i(TAG, "onAttach: ");
//        getActivity().setTitle("Device List");
        if (getActivity() instanceof Callback) {
            callback = (Callback) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Log.i(TAG, "onDetach: ");
        callback = null;
    }

    public interface Callback{
        void requestConnectDevice(BluetoothDevice device);
    }
}
