package com.ucmap.bluetoothsearch.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.Log;

import com.ucmap.bluetoothsearch.utils.ClsUtils;

/**
 * 作者: Justson
 * 时间:2016/10/2 13:55.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class PairReceiver extends BroadcastReceiver {

    private BluetoothDevice mBluetoothDevice;

    private String target;

    private PairCallBack mPairCallBack;

    public PairReceiver(String target, PairCallBack pairCallBack) {
        this.target = target;
        this.mPairCallBack = pairCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mBluetoothDevice.getName().equals(target)) {
                try {
                    ClsUtils.createBond(mBluetoothDevice.getClass(), mBluetoothDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            Log.i("Infoss", "action");
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mBluetoothDevice.getName().equals(target)) {
                try {
                    abortBroadcast();//终止有序广播
                    ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                    if (mPairCallBack != null)
                        mPairCallBack.onPairFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Infoss", "不是目标设备:" + mBluetoothDevice.getName());
            }

        }

    }

    interface PairCallBack {
        void onPairFinish();
    }
}
