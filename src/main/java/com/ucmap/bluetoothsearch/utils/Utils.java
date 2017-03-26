package com.ucmap.bluetoothsearch.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;

/**
 * 作者: Justson
 * 时间:2016/10/3 11:26.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class Utils {


    public static String getCurrentTime(String m) {

        String result = m;
        if (TextUtils.isEmpty(m))
            result = "yyyy-MM-dd hh:mm:ss";
        long time = System.currentTimeMillis();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(result);
        return mSimpleDateFormat.format(time);

    }

}
