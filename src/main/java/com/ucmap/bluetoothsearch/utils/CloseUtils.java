package com.ucmap.bluetoothsearch.utils;

import java.io.Closeable;

/**
 * Created by cenxiaozhong on 2017/3/21.
 */

public class CloseUtils {


    public static void close(Closeable... closeables){

        if(closeables==null)
            return;

        try{
            for(Closeable mCloseable:closeables)
                mCloseable.close();

        }catch (Exception e){

        }


    }

}
