package com.soowin.cleverdog.utlis;


/**
 * Created by Administrator on 2017/6/19.
 */

public class StrUtils {
    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        str = str.replace(" ", "");
        if (str.length() < 1)
            return true;
        return false;
    }

}
