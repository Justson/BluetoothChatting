package com.ucmap.bluetoothsearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者: Justson
 * 时间:2016/10/3 9:48.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class Message implements Parcelable {

    private int id;
    private String name;
    private String mac;
    private String content;
    private String time;

    public Message(String content, int id, String name, String mac, String time) {
        this.content = content;
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Message(String content, int id, String mac, String name) {
        this.content = content;
        this.id = id;
        this.mac = mac;
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Message(Parcel in) {

        id = in.readInt();
        name = in.readString();
        mac = in.readString();
        content = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mac);
        dest.writeString(content);
    }
}
