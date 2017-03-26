package com.ucmap.bluetoothsearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 作者: Justson
 * 时间:2016/10/3 10:13.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public abstract class Adapter<T, E extends Adapter.Holder> extends RecyclerView.Adapter<E> {

    private List<T> mList;

    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    private final LayoutInflater mLayoutInflater;


    protected Adapter(Context context, List<T> list) {
        this.mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(position);
    }

    protected abstract int getItemType(int position);

//    @Override
//    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//    }


    @Override
    public E onCreateViewHolder(ViewGroup parent, int viewType) {
        return (E) getHolder(mLayoutInflater.inflate(getTypeViewResId(viewType), parent, false), parent, viewType).setType(viewType);
    }

    protected abstract E getHolder(View view, ViewGroup parent, int ViewType);

    protected abstract int getTypeViewResId(int type);


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static abstract class Holder extends RecyclerView.ViewHolder {

        int type;

        public int getType() {
            return type;
        }

        public Holder setType(int type) {
            this.type = type;
            return this;
        }

        public Holder(View itemView) {
            super(itemView);
            onSaveSubView(itemView);
        }

        protected abstract void onSaveSubView(View itemView);
    }
}
