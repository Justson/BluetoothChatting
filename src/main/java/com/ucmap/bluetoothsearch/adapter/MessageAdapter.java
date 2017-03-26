package com.ucmap.bluetoothsearch.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucmap.bluetoothsearch.main.BluetoothServer;
import com.ucmap.bluetoothsearch.R;
import com.ucmap.bluetoothsearch.entity.Message;

import java.util.List;

/**
 * 作者: Justson
 * 时间:2016/10/3 10:37.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class MessageAdapter extends Adapter<Message, MessageAdapter.MessageHolder> {
    private Context mContext;
    private List<Message> mList;

    public MessageAdapter(Context context, List<Message> list) {
        super(context, list);
        mContext = context;
        mList = list;
    }

    @Override
    protected int getItemType(int position) {
        if (mList.get(position).getId() == BluetoothServer.MESSAGE_READ) {
            return TYPE_2;
        } else if (mList.get(position).getId() == BluetoothServer.MESSAGE_WRITE) {
            return TYPE_1;
        } else {
            return 0;
        }
    }

    @Override
    protected MessageHolder getHolder(View view, ViewGroup parent, int ViewType) {
        return new MessageHolder(view);
    }

    @Override
    protected int getTypeViewResId(int type) {
        if (type == TYPE_2) {
            return R.layout.layout_read;
        } else if (type == TYPE_1) {

            return R.layout.layout_write;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        String name = mList.get(position).getName();
        if (name.length() > 6) {
//            name = name.substring(name.length() - 4, name.length());
            holder.mNameTextView.setTextSize(8);
        }
        holder.mNameTextView.setText(name);
        int paddingLeft = holder.mContentTextView.getPaddingLeft();
        int paddingRight = holder.mContentTextView.getPaddingRight();
        int paddingTop = holder.mContentTextView.getPaddingTop();
        int paddingBottom = holder.mContentTextView.getPaddingBottom();
        holder.mContentTextView.setText(mList.get(position).getContent());
        if (holder.getType() == TYPE_1) {
            holder.mContentTextView.setBackgroundResource(R.mipmap.chatto_bg_normal);

        } else if (holder.getType() == TYPE_2) {
            holder.mContentTextView.setBackgroundResource(R.mipmap.chatfrom_bg_normal);
        }
        holder.mContentTextView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        holder.mTimeTextView.setText(mList.get(position).getTime());
    }


    static class MessageHolder extends Adapter.Holder {


        private TextView mTimeTextView;
        private TextView mNameTextView;
        private TextView mContentTextView;

        public MessageHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onSaveSubView(View itemView) {
            mTimeTextView = (TextView) itemView.findViewById(R.id.chat_time);
            mNameTextView = (TextView) itemView.findViewById(R.id.chat_name);
            mContentTextView = (TextView) itemView.findViewById(R.id.chat_content);
        }
    }
}
