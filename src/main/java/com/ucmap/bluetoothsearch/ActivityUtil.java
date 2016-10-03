package com.ucmap.bluetoothsearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 作者: Justson
 * 时间:2016/10/2 14:54.
 * 邮箱: cenxiaozhong.qqcom@qq.com
 * 公司: YGS
 */

public class ActivityUtil {


    public static void startFragment(FragmentManager fm, Fragment fragment,int resId){
        Fragment mFragment=fm.findFragmentByTag(fragment.getClass().getName());
        FragmentTransaction ft=fm.beginTransaction();
        if(mFragment==null){
            ft.add(resId,fragment,fragment.getClass().getName());
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

}
