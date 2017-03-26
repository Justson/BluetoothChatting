package com.ucmap.bluetoothsearch.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ucmap.bluetoothsearch.main.BluetoothServer;
import com.ucmap.bluetoothsearch.adapter.CommonAdapter;
import com.ucmap.bluetoothsearch.adapter.MessageAdapter;
import com.ucmap.bluetoothsearch.R;
import com.ucmap.bluetoothsearch.utils.ClsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * 作者: Justson
 * 时间:2016/10/2 14:49.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class BluetoothFragment extends Fragment {

    @Bind(R.id.device_name)
    TextView mDeviceName;
    @Bind(R.id.back)
    LinearLayout mBack;
    @Bind(R.id.device)
    FrameLayout mDevice;
    @Bind(R.id.sendButton)
    Button mSendButton;
    @Bind(R.id.inputEdit)
    EditText mInputEdit;
    @Bind(R.id.bottom_na)
    RelativeLayout mBottomNa;
    @Bind(R.id.content_recyclerview)
    RecyclerView mContentRecyclerview;
    private MaterialDialog mDialog;
    private View mContentView;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mLists = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private LinearLayout mLinearLayout;
    private SearchReceiver mSearchReceiver;
    private final List<com.ucmap.bluetoothsearch.entity.Message> messages = new ArrayList<>();
    private BluetoothServer mBluetoothServer;
    private MessageAdapter mMessageAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int what = msg.what;
            switch (what) {

                case BluetoothServer.MESSAGE_CONNECTED:
                    Toast.makeText(mContext, "Conected sucess:" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    mDeviceName.setText(msg.obj.toString());
                    break;
                case BluetoothServer.MESSAGE_CONNECT_FAIL:
                    Toast.makeText(mContext, "Conected Fail", Toast.LENGTH_SHORT).show();
                    if (mDeviceName != null)
                        mDeviceName.setText("Disconnection");
                    break;
                case BluetoothServer.CONNECT_CUT:
                    Toast.makeText(mContext, "Connect Cut", Toast.LENGTH_SHORT).show();
                    connectCut();
                    break;
                case BluetoothServer.MESSAGE_READ:
                    addMessage((com.ucmap.bluetoothsearch.entity.Message) msg.obj);
//                    Toast.makeText(mContext, messages.get(messages.size() - 1).getContent(), Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothServer.MESSAGE_WRITE:
                    com.ucmap.bluetoothsearch.entity.Message mMessage = (com.ucmap.bluetoothsearch.entity.Message) msg.obj;
                    addMessage((com.ucmap.bluetoothsearch.entity.Message) msg.obj);
//                    Toast.makeText(mContext, messages.get(messages.size() - 1).getContent(), Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothServer.MESSAGE_DISCONNECTION:
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    if (mDeviceName != null)
                        mDeviceName.setText("Disconnection");
                    break;
            }

        }
    };

    //connection was be cut
    private void connectCut() {
        if (mDeviceName != null)
            mDeviceName.setText("Disconnection");
        if (messages != null) {
            messages.clear();
            if (mMessageAdapter != null)
                mMessageAdapter.notifyDataSetChanged();
        }
    }

    private void addMessage(com.ucmap.bluetoothsearch.entity.Message message) {
        messages.add(message);
        if (mMessageAdapter == null)
            return;
        mMessageAdapter.notifyItemInserted(messages.size() - 1);
        mContentRecyclerview.scrollToPosition(messages.size() - 1);

    }


    public static BluetoothFragment getInstantce(Bundle bundle) {

        BluetoothFragment mBluetoothFragment = new BluetoothFragment();
        if (bundle != null)
            mBluetoothFragment.setArguments(bundle);
        return mBluetoothFragment;
    }

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            mDeviceName.setText("Disconnection");
            initData();
        }
    }

    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSearchReceiver = new SearchReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        mContext.registerReceiver(mSearchReceiver, mIntentFilter);


        //init content recyclerview
        mMessageAdapter = new MessageAdapter(this.getActivity(), messages);
        mContentRecyclerview.setAdapter(mMessageAdapter);
        mContentRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mContentRecyclerview.setItemAnimator(new DefaultItemAnimator());

        //init bluetoothserver
        mBluetoothServer = new BluetoothServer(mHandler);
        mBluetoothServer.start();

        //测试配对过程
//        mDevice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                if (msBluetoothDevice != null) {
//                    try {
//                        ClsUtils.removeBond(msBluetoothDevice.getClass(), msBluetoothDevice);
//                    } catch (Exception e) {
//
//                    }
//                }
//
//                return false;
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.back, R.id.device, R.id.sendButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                toFinish();
                break;
            case R.id.device:
                showDevices();
                break;
            case R.id.sendButton:
                sendMessage();
                break;
        }
    }

    MaterialDialog mMaterialDialog_ = null;

    private void toFinish() {

        if(mMaterialDialog_==null){
            mMaterialDialog_ = new MaterialDialog(getActivity())//
                    .setMessage("Are you sure to exit ?")//
                    .setPositiveButton("Sure", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.exit(0);
                        }
                    })//
                    .setNegativeButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog_.dismiss();
                        }
                    });
        }

        mMaterialDialog_.show();
    }

    //发送消息
    private void sendMessage() {
        String msg = mInputEdit.getText().toString();
        if (mBluetoothServer == null || TextUtils.isEmpty(msg) || mBluetoothServer.getCurrentState() != BluetoothServer.BluetoothState.CONNECTED)
            return;
        mInputEdit.setText("");
        Log.i("Infoss", "send msg:" + msg);
        mBluetoothServer.sendMessage(msg);

    }

    //search bluetooth device
    private void showDevices() {
        if (mDialog == null) {
            mDialog = new MaterialDialog(this.getActivity());
            mLists.addAll((List) Arrays.asList(mBluetoothAdapter.getBondedDevices().toArray()));
            mCommonAdapter = new CommonAdapter(this.getActivity(), mLists);
            mDialog.setNegativeButton("Search", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLinearLayout != null)
                        mLinearLayout.setVisibility(View.VISIBLE);

                    mBluetoothAdapter.startDiscovery();
                }
            });
            mDialog.setPositiveButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDialog != null)
                        mDialog.dismiss();
                }
            });
        }
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.list_devices, null);
        ListView mListView = (ListView) mContentView.findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();
                mDialog.dismiss();
                isPair = true;
                if (!BluetoothAdapter.checkBluetoothAddress(mLists.get(position).getAddress()))
                    return;
                connect(mLists.get(position));
            }
        });
        mLinearLayout = (LinearLayout) mContentView.findViewById(R.id.linearLayout);
        mListView.setAdapter(mCommonAdapter);
        mDialog.setContentView(mContentView);
        mDialog.show();
    }

    private String connectedDeviceName = "";


    private void bond(BluetoothDevice bluetoothDevice) {

        try {
            ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
            Log.i("Infoss", "bond");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BluetoothDevice msBluetoothDevice;

    private void connect(BluetoothDevice bluetoothDevice) {

        msBluetoothDevice = bluetoothDevice;
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE && isPair) {
            bond(bluetoothDevice);
        }

        connectedDeviceName = bluetoothDevice.getName();
        mDeviceName.setText(bluetoothDevice.getName() + "  ...");
        mBluetoothServer.connect(bluetoothDevice);
    }

    private void search() {

    }

    private void addDevice(BluetoothDevice bluetoothDevice) {

        mLists.add(bluetoothDevice);
        Log.i("infoss", "list:" + mLists.size());
        mCommonAdapter.notifyDataSetChanged();
    }


    class SearchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Log.i("Infoss", "Name:" + mBluetoothDevice.getName() + "   mac:" + mBluetoothDevice.getAddress());
                if (mBluetoothDevice != null)
                    addDevice(mBluetoothDevice);
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (mLinearLayout != null)
                    mLinearLayout.setVisibility(View.GONE);
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        break;
                }
            }
            if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {


                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    abortBroadcast();
                    ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                    //3.调用setPin方法进行配对...
                    boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, "1234");
//                    Log.i("Infoss", "confirm");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (action.equals(BluetoothDevice.ACTION_PAIRING_CANCEL)) {

                Log.i("Infoss", "pair cancel");
                isPair = false;
                connect(msBluetoothDevice);
            }

        }
    }

    private boolean isPair = true;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSearchReceiver != null) {
            mContext.unregisterReceiver(mSearchReceiver);
        }
        messages.clear();
        if (mBluetoothServer != null) {
            mBluetoothServer.close();
            mBluetoothServer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
