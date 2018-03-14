package com.cting.support.robinbt.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cting.support.robinbt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChatFragment extends Fragment {
    public static final String TAG = "cting/ChatFragment";
    public static final int MSG_WRITE = 1;

    @BindView(R.id.conversation_text)
    TextView conversationText;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.send_btn)
    Button sendBtn;

    private Unbinder unbinder;

    private BluetoothAdapter mBtAdapter;

    private Callback callback;

    /*private static final int MSG_READ = 1;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_READ:
                    conversationText.setText((CharSequence) msg.obj);
                    break;
            }
        }
    };*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Log.i(TAG, "onAttach: ");
//        String title = TextUtils.isEmpty(mBtAdapter.getName()) ? mBtAdapter.getAddress() : mBtAdapter.getName();
//        getActivity().setTitle(title);
        if (getActivity() instanceof Callback) {
            callback = (Callback) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @OnClick(R.id.send_btn)
    public void onViewClicked() {
        Log.i(TAG, "click send");
        if (callback != null) {
            callback.send(editText.getText().toString());
        }
    }

    public void setText(String content) {
        editText.getText().clear();
        conversationText.append(content);
        conversationText.append("\n");
    }

    public interface Callback {
        void send(String message);
    }

}
