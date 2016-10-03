package com.ucmap.bluetoothsearch;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 作者: Justson
 * 时间:2016/10/2 16:41.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class CommonAdapter extends BaseAdapter {
    private final List<BluetoothDevice> devices;
    private final LayoutInflater mLayoutInflater;

    public CommonAdapter(Context context, List<BluetoothDevice> devices) {
        this.devices = devices;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
            mViewHolder.name = (TextView) convertView.findViewById(R.id.device_name);
            mViewHolder.mac = (TextView) convertView.findViewById(R.id.device_mac);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.name.setText(devices.get(position).getName());
        mViewHolder.mac.setText(devices.get(position).getAddress());
        return convertView;
    }


    static class ViewHolder {
        TextView name;
        TextView mac;
    }

}
