package com.ucmap.bluetoothsearch.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.ucmap.bluetoothsearch.R;
import com.ucmap.bluetoothsearch.utils.ActivityUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BluetoothManager mBluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();


        ActivityUtil.startFragment(this.getSupportFragmentManager(), BluetoothFragment.getInstantce(null), R.id.activity_main);

    }

}
